package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.FindJudgeGoalRequest;
import org.dows.hep.api.base.indicator.response.JudgeGoalResponse;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.request.FindInterveneList4ExptRequest;
import org.dows.hep.api.user.experiment.request.SaveExptJudgeGoalRequest;
import org.dows.hep.api.user.experiment.response.ExptJudgeGoalResponse;
import org.dows.hep.api.user.experiment.response.SaveExptOperateResponse;
import org.dows.hep.api.user.experiment.vo.ExptJudgeGoalItemVO;
import org.dows.hep.biz.base.indicator.JudgeGoalBiz;
import org.dows.hep.biz.dao.OperateOrgFuncDao;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 判断指标操作
 *
 * @author : wuzl
 * @date : 2023/10/19 10:21
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExperimentJudgeBiz {

    private final JudgeGoalBiz judgeGoalBiz;

    private final OperateOrgFuncDao operateOrgFuncDao;

    public List<JudgeGoalResponse> listJudgeGoal(FindInterveneList4ExptRequest req){
        ExptRequestValidator.create(req).checkExperimentInstanceId();
        FindJudgeGoalRequest castReq= FindJudgeGoalRequest.builder()
                .appId(req.getAppId())
                .indicatorFuncId(req.getIndicatorFuncId())
                .build();
        return judgeGoalBiz.listJudgeGoal(castReq);
    }

    public ExptJudgeGoalResponse getJudgeGoal(ExptOperateOrgFuncRequest req){
        return getReportSnapData(req, false, false, ExptJudgeGoalResponse.class, ExptJudgeGoalResponse::new);
    }

    public SaveExptOperateResponse saveJudgeGoal(SaveExptJudgeGoalRequest req, HttpServletRequest httpReq){
        ExptRequestValidator validator=ExptRequestValidator.create(req)
                .checkExperimentPerson()
                .checkExperimentOrgId()
                .checkExperimentInstanceId();

        req.setGoalItems(ShareUtil.XObject.defaultIfNull(req.getGoalItems(), Collections.emptyList()));
        AssertUtil.trueThenThrow(req.getGoalItems().stream()
                        .map(ExptJudgeGoalItemVO::getIndicatorJudgeGoalId)
                        .collect(Collectors.toSet())
                        .size()<req.getGoalItems().size())
                .throwMessage("存在重复的目标项目，请检查");
        //校验操作类型
        final EnumExptOperateType operateType=EnumExptOperateType.JUDGEHealthGoal;
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(httpReq);
        //校验挂号
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .checkOrgFlow(true);

        //保存操作记录
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay())
                .setPeriods(timePoint.getPeriod())
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(operateType.getReportFuncFlag()?1:0)
                .setReportLabel("管理目标")
                .setReportDescr("");
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        ExptJudgeGoalResponse snapRst=new ExptJudgeGoalResponse().setGoalItems(req.getGoalItems());
        try{
            rowOrgFuncSnap.setInputJson(JacksonUtil.toJson(snapRst,true));
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据编制失败：%s",ex.getMessage()),ex);
        }

        boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc, Arrays.asList(rowOrgFuncSnap),false);
        return new SaveExptOperateResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());
    }

    private <T> T getReportSnapData(ExptOperateOrgFuncRequest reqOperateFunc, boolean checkIndicatorFunc, boolean checkOrgFlow, Class<T> clazz, Supplier<T> creator){
        T rst=creator.get();
        ExptRequestValidator validator=ExptRequestValidator.create(reqOperateFunc)
                .checkExperimentPerson()
                .checkExperimentOrgId()
                .checkExperimentInstanceId();
        reqOperateFunc.setExperimentOrgId(validator.getExperimentOrgId());
        if(checkIndicatorFunc){
            validator.checkIndicatorFunc();
        }
        if(checkOrgFlow&&ShareUtil.XObject.isEmpty(reqOperateFunc.getOperateFlowId())) {
            ExperimentTimePoint timePoint = validator.getTimePoint(false, LocalDateTime.now(), false);
            if (ShareUtil.XObject.isEmpty(timePoint)) {
                return rst;
            }
            ExptOrgFlowValidator flowValidator = ExptOrgFlowValidator.create(validator);
            if (!flowValidator.ifOrgFlowRunning(false, timePoint.getPeriod())) {
                return rst;
            }
            reqOperateFunc.setOperateFlowId(flowValidator.getOperateFlowId());
        }
        OperateOrgFuncEntity rowOrgFunc=getRowOrgFunc(reqOperateFunc,
                OperateOrgFuncEntity::getOperateOrgFuncId,
                OperateOrgFuncEntity::getOperateFlowId,
                OperateOrgFuncEntity::getPeriods)
                .orElse(null);
        if(null==rowOrgFunc){
            return rst;
        }
        List<OperateOrgFuncSnapEntity> rowOrgFuncSnaps=operateOrgFuncDao.getSubByLeadId(rowOrgFunc.getOperateOrgFuncId(),OperateOrgFuncSnapEntity::getInputJson);
        if(ShareUtil.XObject.anyEmpty(rowOrgFuncSnaps,()->rowOrgFuncSnaps.get(0).getInputJson())){
            return rst;
        }
        try{
            return JacksonUtil.fromJson(rowOrgFuncSnaps.get(0).getInputJson(), clazz);
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据解析失败：%s",ex.getMessage()),ex);
        }
        return rst;
    }

    private OperateOrgFuncEntity createRowOrgFunc(ExptRequestValidator req){
        return OperateOrgFuncEntity.builder()
                .appId(req.getAppId())
                .experimentInstanceId(req.getExperimentInstanceId())
                .experimentGroupId(req.getExperimentGroupId())
                .experimentOrgId(req.getExperimentOrgId())
                .experimentPersonId(req.getExperimentPersonId())
                .periods(req.getPeriods())
                .indicatorCategoryId(req.getIndicatorCategoryId())
                .indicatorFuncId(req.getIndicatorFuncId())
                .build();
    }

    private Optional<OperateOrgFuncEntity> getRowOrgFunc(ExptOperateOrgFuncRequest req, SFunction<OperateOrgFuncEntity,?>... cols){
        return operateOrgFuncDao.getCurrentOrgFuncRecord(req.getExperimentPersonId(), req.getExperimentOrgId(),
                req.getIndicatorFuncId(), req.getPeriods(),req.getOperateFlowId(), cols);
    }
}
