package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.dao.CaseOrgFeeDao;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.entity.OperateFlowEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 学生端机构挂号校验
 * @author : wuzl
 * @date : 2023/6/5 15:52
 */
public class ExptOrgFlowValidator {

    /**
     *
     * @param appId
     * @param experimentInstanceId 实验id
     * @param experimentOrgId 机构id
     * @param experimentPersonId 实验人物id
     */
    private ExptOrgFlowValidator(String appId,String experimentInstanceId, String experimentOrgId,String experimentPersonId,String caseOrgId){
        this.appId=appId;
        this.experimentInstanceId=experimentInstanceId;
        this.experimentOrgId=experimentOrgId;
        this.experimentPersonId=experimentPersonId;
        this.uimOrgId =caseOrgId;

    }



    //region create
    public static ExptOrgFlowValidator create(ExptRequestValidator req){
        return new ExptOrgFlowValidator(req.getAppId(),req.getExperimentInstanceId(),req.getExperimentOrgId(),req.getExperimentPersonId(),req.getUimOrgId());
    }
    public static ExptOrgFlowValidator create(String appId,String experimentInstanceId, String experimentOrgId,String experimentPersonId){
        return create(ExptRequestValidator.create(appId,experimentInstanceId,null,experimentOrgId,experimentPersonId).checkExperimentOrg());
    }

    public static ExptOrgFlowValidator create(String appId,String experimentInstanceId, String experimentOrgId,String experimentPersonId,String uimOrgId){
        return new ExptOrgFlowValidator(appId, experimentInstanceId, experimentOrgId, experimentPersonId, uimOrgId);
    }

    //endregion

    //region fileds
    //应用ID
    @Getter
    private String appId;
    //实验id
    @Getter
    private String experimentInstanceId;
    //实验人物ID
    @Getter
    private String experimentPersonId;
    //实验机构ID
    @Getter
    private String experimentOrgId;
    //uim机构ID
    @Getter
    private String uimOrgId;

    //挂号流程id
    @Getter
    private String operateFlowId;

    @Getter
    private List<CaseOrgFeeEntity> exptFeeList;


    @Getter
    private Optional<OperateFlowEntity> exptFlow;
    //endregion


    //region 机构费用

    /**
     * 获取挂号费
     * @return
     */
    public Optional<Double> getOrgFee4Ghf(){
        return getOrgFee(EnumOrgFeeType.GHF)
                .map(CaseOrgFeeEntity::getFee)
                .filter(i->i>0);
    }

    /**
     * 获取机构费用设置
     * @param feeType
     * @return
     */
    public Optional<CaseOrgFeeEntity> getOrgFee(EnumOrgFeeType feeType){
        List<CaseOrgFeeEntity> fees=getOrgFeeList(false);
        return fees.stream()
                .filter(i->feeType.getCode().equalsIgnoreCase( i.getFeeCode() ))
                .findFirst();

    }

    public ExptOrgFlowValidator checkOrgFeeList(boolean assertExists){
        getOrgFeeList(assertExists);
        return this;
    }
    public ExptOrgFlowValidator checkOrgFeeList(boolean assertExists, SFunction<CaseOrgFeeEntity,?>... cols){
        getOrgFeeList(assertExists,cols);
        return this;
    }
    public List<CaseOrgFeeEntity> getOrgFeeList(boolean assertExists){
        return getOrgFeeList(assertExists, CaseOrgFeeEntity::getCaseOrgId,
                CaseOrgFeeEntity::getFeeCode,
                CaseOrgFeeEntity::getFeeName,
                CaseOrgFeeEntity::getFee,
                CaseOrgFeeEntity::getReimburseRatio);
    }
    public List<CaseOrgFeeEntity> getOrgFeeList(boolean assertExists, SFunction<CaseOrgFeeEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(uimOrgId))
                .throwMessage("未找到案例机构ID");
        if(null==exptFeeList){
            exptFeeList= CrudContextHolder.getBean(CaseOrgFeeDao.class)
                    .getFeeList(uimOrgId, cols);
        }
        AssertUtil.trueThenThrow(assertExists&&ShareUtil.XObject.isEmpty(this.exptFeeList ))
                .throwMessage("未找到机构费用设置");
        return exptFeeList;
    }
    //endregion

    //region 校验需要挂号且已挂号

    /**
     * 校验需要挂号且已挂号
     * @return
     */
    public ExptOrgFlowValidator requireOrgFlowRunning() {
        ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTime(ExperimentCacheKey.create(this.appId, this.experimentInstanceId),
                LocalDateTime.now(),false);
        return requireOrgFlowRunning(timePoint.getPeriod());
    }
    public ExptOrgFlowValidator requireOrgFlowRunning(int curPeriod){
        Optional<Double> regFeeOption=getOrgFee4Ghf();
        OperateFlowEntity orgFlow= getOrgFlow(false);
        AssertUtil.trueThenThrow(regFeeOption.isPresent()&&!ifOrgFlowRunning(orgFlow,curPeriod))
                .throwMessage("请先挂号");
        return this;
    }
    //endregion

    //region 是否有已有当期挂号
    /**
     * 是否有已有挂号
     * @return
     */
    public ExptOrgFlowValidator checkOrgFlowRunning() {
        ExperimentTimePoint timePoint = ExperimentSettingCache.Instance().getTimePointByRealTime(ExperimentCacheKey.create(this.appId, this.experimentInstanceId),
                LocalDateTime.now(), false);
        return checkOrgFlowRunning(timePoint.getPeriod());
    }
    public ExptOrgFlowValidator checkOrgFlowRunning( int curPeriod) {
        AssertUtil.falseThenThrow(ifOrgFlowRunning(true, curPeriod))
                .throwMessage("未找到挂号记录");
        return this;
    }
    public boolean ifOrgFlowRunning(boolean assertExists){
        ExperimentTimePoint timePoint= ExperimentSettingCache.Instance().getTimePointByRealTime(ExperimentCacheKey.create(this.appId, this.experimentInstanceId),
                LocalDateTime.now(),false);

        return ifOrgFlowRunning(assertExists, timePoint.getPeriod());
    }
    public boolean ifOrgFlowRunning(boolean assertExists, int curPeriod){
        return ifOrgFlowRunning(getOrgFlow(assertExists), curPeriod);
    }
    public static boolean ifOrgFlowRunning(OperateFlowEntity rowFlow,int curPeriod){
        return null!=rowFlow
                &&null==rowFlow.getEndTime()
                &&curPeriod<= rowFlow.getPeriods();
    }
    //endregion

    //region 获取最新挂号记录
    /**
     * 获取最新挂号记录
     * @param assertExists
     * @return
     */
    public ExptOrgFlowValidator checkOrgFlow(boolean assertExists){
        getOrgFlow(assertExists);
        return this;
    }
    public ExptOrgFlowValidator checkOrgFlow(boolean assertExists,SFunction<OperateFlowEntity,?>... cols){
        getOrgFlow(assertExists, cols);
        return this;
    }
    public OperateFlowEntity getOrgFlow(boolean assertExists){
        return getOrgFlow(assertExists,
                OperateFlowEntity::getOperateFlowId,
                OperateFlowEntity::getStartTime,
                OperateFlowEntity::getEndTime,
                OperateFlowEntity::getPeriods,
                OperateFlowEntity::getTotalSteps,
                OperateFlowEntity::getDoneSteps);
    }
    public OperateFlowEntity getOrgFlow(boolean assertExists,SFunction<OperateFlowEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentPersonId))
                .throwMessage("未找到实验人物ID");
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentOrgId))
                .throwMessage("未找到实验机构ID");
        if(null==exptFlow){
            exptFlow=CrudContextHolder.getBean(OperateFlowDao.class)
                    .getCurrrentFlow(experimentPersonId, experimentOrgId, null, cols);
        }
        AssertUtil.trueThenThrow(assertExists&&exptFlow.isEmpty())
                .throwMessage("未找到挂号记录");
        exptFlow.filter(i->ShareUtil.XObject.notEmpty(i.getOperateFlowId()))
                .ifPresent( i->operateFlowId=i.getOperateFlowId());
        return exptFlow.orElse(null);
    }
    //endregion

}
