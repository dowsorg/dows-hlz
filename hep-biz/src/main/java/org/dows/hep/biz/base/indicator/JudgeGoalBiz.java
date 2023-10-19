package org.dows.hep.biz.base.indicator;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.*;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.indicator.response.JudgeGoalInfoResponse;
import org.dows.hep.api.base.indicator.response.JudgeGoalResponse;
import org.dows.hep.api.base.intervene.request.DelRefIndicatorRequest;
import org.dows.hep.biz.dao.IndicatorExpressionRefDao;
import org.dows.hep.biz.dao.IndicatorFuncDao;
import org.dows.hep.biz.dao.IndicatorJudgeGoalDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.IndicatorJudgeGoalEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/18 16:41
 */
@Service
@RequiredArgsConstructor
public class JudgeGoalBiz {

    private final IndicatorJudgeGoalDao indicatorJudgeGoalDao;

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    private final IndicatorExpressionRefDao indicatorExpressionRefDao;

    private final IndicatorFuncDao indicatorFuncDao;

    /**
     * 获取管理目标列表
     * @param
     * @return
     */
    public Page<JudgeGoalResponse> pageJudgeGoal(FindJudgeGoalRequest findJudgeGoal ) {
        return ShareBiz.buildPage(indicatorJudgeGoalDao.pageByCondition(findJudgeGoal), i ->
                CopyWrapper.create(JudgeGoalResponse::new).endFrom(i));

    }

    /**
     * 获取管理目标详细信息
     * @param
     * @return
     */
    public JudgeGoalInfoResponse getJudgeGoal(GetJudgeGoalRequest getJudgeGoal) {
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(getJudgeGoal.getIndicatorJudgeGoalId()))
                .throwMessage("管理目标ID不可为空");
        IndicatorJudgeGoalEntity row= AssertUtil.getNotNull(indicatorJudgeGoalDao.getById(getJudgeGoal.getIndicatorJudgeGoalId()))
                .orElseThrow("管理目标不存在");
        List<IndicatorExpressionResponseRs> expressions=ShareBiz.getExpressionsByReasonId(indicatorExpressionBiz,getJudgeGoal.getAppId(),getJudgeGoal.getIndicatorJudgeGoalId());
        return CopyWrapper.create(JudgeGoalInfoResponse::new).endFrom(row)
                .setIndicatorExpressionResponseRsList(expressions);
    }

    /**
     * 保存管理目标
     * @param
     * @return
     */
    public Boolean saveJudgeGoal(SaveJudgeGoalRequest saveJudgeGoal ) {
        checkIndicatorFunc(saveJudgeGoal.getIndicatorFuncId());
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveJudgeGoal.getIndicatorJudgeGoalId())
                        && indicatorJudgeGoalDao.getById(saveJudgeGoal.getIndicatorJudgeGoalId(), IndicatorJudgeGoalEntity::getId).isEmpty())
                .throwMessage("管理目标不存在");

        IndicatorJudgeGoalEntity row = CopyWrapper.create(IndicatorJudgeGoalEntity::new)
                .endFrom(saveJudgeGoal);
        return indicatorJudgeGoalDao.tranSave(row, true, () -> {
            if (ShareUtil.XObject.isEmpty(saveJudgeGoal.getJudgeRuleExpresssions())) {
                return true;
            }
            return indicatorExpressionRefDao.tranUpdateReasonId(row.getIndicatorJudgeGoalId(), saveJudgeGoal.getJudgeRuleExpresssions());
        });
    }

    /**
     * 启用禁用管理目标
     *
     * @param
     * @return
     */
    public Boolean setJudgeGoalState(SetJudgeGoalStateRequest setJudgeGoalStateRequest ) {
        return indicatorJudgeGoalDao.tranSetState(setJudgeGoalStateRequest.getIndicatorJudgeGoalId(), setJudgeGoalStateRequest.getState());
    }

    /**
     * 删除管理目标
     * @param
     * @return
     */
    public Boolean delJudgeGoal(DelJudgeGoalRequest delJudgeGoal ) {
        return indicatorJudgeGoalDao.tranDelete(delJudgeGoal.getIds());
    }

    /**
     * 删除公式
     * @param delRefIndicator
     * @return
     */
    public Boolean delRefExpression(DelRefIndicatorRequest delRefIndicator ) {
        return indicatorExpressionRefDao.tranDeleteByExpressionId(delRefIndicator.getIds());
    }

    private IndicatorFuncEntity checkIndicatorFunc(String indicatorFuncId){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(indicatorFuncId))
                .throwMessage("功能点ID不可为空");
        return AssertUtil.getNotNull(indicatorFuncDao.getById(indicatorFuncId,
                        IndicatorFuncEntity::getIndicatorFuncId,
                        IndicatorFuncEntity::getName,
                        IndicatorFuncEntity::getPid,
                        IndicatorFuncEntity::getIndicatorCategoryId))
                .orElseThrow("功能点不存在");
    }
}
