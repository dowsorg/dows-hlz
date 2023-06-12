package org.dows.hep.biz.user.experiment;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthManagementGoalRequest;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:实验:机构操作-判断指标
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class ExperimentOrgJudgeBiz {
    private final ExperimentIndicatorJudgeRiskFactorService experimentIndicatorJudgeRiskFactorService;
    private final ExperimentIndicatorJudgeHealthGuidanceService experimentIndicatorJudgeHealthGuidanceService;
    private final ExperimentIndicatorJudgeHealthProblemService experimentIndicatorJudgeHealthProblemService;
    private final ExperimentIndicatorJudgeDiseaseProblemService experimentIndicatorJudgeDiseaseProblemService;
    private final ExperimentPersonPropertyService experimentPersonPropertyService;
    private final IndicatorJudgeHealthManagementGoalService indicatorJudgeHealthManagementGoalService;
    private final OperateOrgFuncService operateOrgFuncService;
    private final OperateOrgFuncSnapService operateOrgFuncSnapService;

    /**
     * @param
     * @return
     * @说明: 健康问题+健康指导：获取问题列表（含分类）
     * @关联表: indicator_judge_health_problem, indicator_judge_health_guidance, OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<OrgJudgeItemsResponse> listOrgJudgeItems(FindOrgJudgeItemsRequest findOrgJudgeItems) {
        return new ArrayList<OrgJudgeItemsResponse>();
    }

    /**
     * @param
     * @return
     * @说明: 疾病问题：获取检查类别+项目
     * @关联表: indicator_judge_disease_problem，OperateOrgFunc,OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<OrgJudgeCategResponse> listOrgJudgeCategs(FindOrgJudgeCategsRequest findOrgJudgeCategs) {
        return new ArrayList<OrgJudgeCategResponse>();
    }

    /**
     * @param
     * @return
     * @说明: 健康问题+健康指导+疾病问题：获取最新保存列表
     * @关联表: indicator_judge_health_problem, indicator_judge_health_guidance, indicator_judge_disease_problem，OperateOrgFunc,OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<OrgJudgedItemsResponse> listOrgJudgedItems(FindOrgJudgedItemsRequest findOrgJudgedItems) {
        return new ArrayList<OrgJudgedItemsResponse>();
    }

    /**
     * @param
     * @return
     * @说明: 健康问题+健康指导+疾病问题：保存
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public SaveOrgJudgeResponse saveOrgJudge(SaveOrgJudgeRequest saveOrgJudge) {
        return new SaveOrgJudgeResponse();
    }

    /**
     * @param
     * @return
     * @说明: 健管目标：获取健管目标列表
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public OrgJudgeGoalsResponse listOrgJudgeGoals(FindOrgJudgeGoalsRequest findOrgJudgeGoals) {
        return new OrgJudgeGoalsResponse();
    }

    /**
     * @param
     * @return
     * @说明: 健管目标：保存，包含是否购买保险
     * @关联表: OperateOrgFunc, OperateOrgFuncSnap
     * @工时: 6H
     * @开发者: wuzl
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public SaveOrgJudgeGoalsResponse saveOrgJudgeGoals(SaveOrgJudgeGoalsRequest saveOrgJudgeGoals) {
        return new SaveOrgJudgeGoalsResponse();
    }

    /**
     * @param
     * @return
     * @说明: 获取二级类无报告的判断指标信息
     * @关联表: indicatorJudgeRiskFactor
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月26日 上午9:49:34
     */
    public Map<String, List<ExperimentIndicatorJudgeRiskFactorResponse>> getIndicatorJudgeRiskFactor(String indicatorFuncId) {
        //1、根据指标功能ID获取所有的分类
        List<ExperimentIndicatorJudgeRiskFactorEntity> entityList = experimentIndicatorJudgeRiskFactorService.lambdaQuery()
                .select(ExperimentIndicatorJudgeRiskFactorEntity::getId,
                        ExperimentIndicatorJudgeRiskFactorEntity::getExperimentJudgeRiskFactorId,
                        ExperimentIndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId,
                        ExperimentIndicatorJudgeRiskFactorEntity::getName,
                        ExperimentIndicatorJudgeRiskFactorEntity::getExperimentIndicatorCategoryId)
                .eq(ExperimentIndicatorJudgeRiskFactorEntity::getExperimentIndicatorFuncId, indicatorFuncId)
                .eq(ExperimentIndicatorJudgeRiskFactorEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgeRiskFactorResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgeRiskFactorResponse response = ExperimentIndicatorJudgeRiskFactorResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgeRiskFactorId(entity.getExperimentJudgeRiskFactorId())
                        .indicatorJudgeRiskFactorId(entity.getIndicatorJudgeRiskFactorId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getExperimentIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        //2、根据分类ID分组
        Map<String, List<ExperimentIndicatorJudgeRiskFactorResponse>> categoryList = responseList.stream().collect(Collectors.groupingBy(ExperimentIndicatorJudgeRiskFactorResponse::getIndicatorCategoryId));
        return categoryList;
    }

    /**
     * @param
     * @return
     * @说明: 获取二级类有报告的判断指标信息
     * @关联表: indicatorJudgeHealthGuidance
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月26日 上午11:51:34
     */
    public Map<String, List<ExperimentIndicatorJudgeHealthGuidanceResponse>> getIndicatorJudgeHealthGuidance(String indicatorFuncId) {
        //1、根据指标功能ID获取所有的分类
        List<ExperimentIndicatorJudgeHealthGuidanceEntity> entityList = experimentIndicatorJudgeHealthGuidanceService.lambdaQuery()
                .select(ExperimentIndicatorJudgeHealthGuidanceEntity::getId,
                        ExperimentIndicatorJudgeHealthGuidanceEntity::getExperimentJudgeHealthGuidanceId,
                        ExperimentIndicatorJudgeHealthGuidanceEntity::getIndicatorJudgeHealthGuidanceId,
                        ExperimentIndicatorJudgeHealthGuidanceEntity::getName,
                        ExperimentIndicatorJudgeHealthGuidanceEntity::getIndicatorCategoryId)
                .eq(ExperimentIndicatorJudgeHealthGuidanceEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(ExperimentIndicatorJudgeHealthGuidanceEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgeHealthGuidanceResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgeHealthGuidanceResponse response = ExperimentIndicatorJudgeHealthGuidanceResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgeHealthGuidanceId(entity.getExperimentJudgeHealthGuidanceId())
                        .indicatorJudgeHealthGuidanceId(entity.getIndicatorJudgeHealthGuidanceId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        //2、根据分类ID分组
        Map<String, List<ExperimentIndicatorJudgeHealthGuidanceResponse>> categoryList = responseList.stream().collect(Collectors.groupingBy(ExperimentIndicatorJudgeHealthGuidanceResponse::getIndicatorCategoryId));
        return categoryList;
    }

    /**
     * @param
     * @return
     * @说明: 三级类别/四级类别：根据指标分类ID获取所有符合条件的数据
     * @关联表: indicatorJudgeHealthProblem
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月26日 下午13:56:34
     */
    public List<ExperimentIndicatorJudgeHealthProblemResponse> getIndicatorJudgeHealthProblemByCategoryId(String indicatoryCategoryId) {
        //1、根据指标分类ID获取所有符合条件的数据
        List<ExperimentIndicatorJudgeHealthProblemEntity> entityList = experimentIndicatorJudgeHealthProblemService.lambdaQuery()
                .select(ExperimentIndicatorJudgeHealthProblemEntity::getId,
                        ExperimentIndicatorJudgeHealthProblemEntity::getExperimentJudgeHealthProblemId,
                        ExperimentIndicatorJudgeHealthProblemEntity::getIndicatorJudgeHealthProblemId,
                        ExperimentIndicatorJudgeHealthProblemEntity::getName,
                        ExperimentIndicatorJudgeHealthProblemEntity::getIndicatorCategoryId)
                .eq(ExperimentIndicatorJudgeHealthProblemEntity::getIndicatorCategoryId, indicatoryCategoryId)
                .eq(ExperimentIndicatorJudgeHealthProblemEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgeHealthProblemResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgeHealthProblemResponse response = ExperimentIndicatorJudgeHealthProblemResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgeHealthProblemId(entity.getExperimentJudgeHealthProblemId())
                        .indicatorJudgeHealthProblemId(entity.getIndicatorJudgeHealthProblemId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 四级类别：根据指标分类ID获取所有符合条件的数据
     * @关联表: indicatorJudgeHealthProblem
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月05日 下午17:22:34
     */
    public List<ExperimentIndicatorJudgeDiseaseProblemResponse> getIndicatorJudgeDiseaseProblemByCategoryId(String indicatoryCategoryId) {
        //1、根据指标分类ID获取所有符合条件的数据
        List<ExperimentIndicatorJudgeDiseaseProblemEntity> entityList = experimentIndicatorJudgeDiseaseProblemService.lambdaQuery()
                .select(ExperimentIndicatorJudgeDiseaseProblemEntity::getId,
                        ExperimentIndicatorJudgeDiseaseProblemEntity::getExperimentJudgeDiseaseProblemId,
                        ExperimentIndicatorJudgeDiseaseProblemEntity::getIndicatorJudgeDiseaseProblemId,
                        ExperimentIndicatorJudgeDiseaseProblemEntity::getName,
                        ExperimentIndicatorJudgeDiseaseProblemEntity::getIndicatorCategoryId)
                .eq(ExperimentIndicatorJudgeDiseaseProblemEntity::getIndicatorCategoryId, indicatoryCategoryId)
                .eq(ExperimentIndicatorJudgeDiseaseProblemEntity::getStatus, true)
                .list();
        List<ExperimentIndicatorJudgeDiseaseProblemResponse> responseList = new ArrayList<>();
        if (entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                ExperimentIndicatorJudgeDiseaseProblemResponse response = ExperimentIndicatorJudgeDiseaseProblemResponse
                        .builder()
                        .id(entity.getId())
                        .experimentJudgeDiseaseProblemId(entity.getExperimentJudgeDiseaseProblemId())
                        .indicatorJudgeDiseaseProblemId(entity.getIndicatorJudgeDiseaseProblemId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        return responseList;
    }

    /**
     * @param
     * @return
     * @说明: 是否购买保险
     * @关联表: experimentPersonProperty
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月26日 下午16:11:34
     */
    @DSTransactional
    public Boolean isPurchaseInsure(String isPurchase, String experimentPersonId) {
        LambdaUpdateWrapper<ExperimentPersonPropertyEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentPersonPropertyEntity>()
                .eq(ExperimentPersonPropertyEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentPersonPropertyEntity::getDeleted, false)
                .set(ExperimentPersonPropertyEntity::getInsuranceState, isPurchase);
        return experimentPersonPropertyService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 判断用户操作正确与否
     * @关联表: indicatorJudgeRiskFactor
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 上午10:11:34
     */
    public Boolean isIndicatorJudgeRiskFactor(List<ExperimentIndicatorJudgeRiskFactorRequest> judgeRiskFactorRequestList) {
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        judgeRiskFactorRequestList.forEach(judgeRiskFactorRequest -> {
            //1、根据ID获取判断规则
            ExperimentIndicatorJudgeRiskFactorEntity entity = experimentIndicatorJudgeRiskFactorService.lambdaQuery()
                    .select(ExperimentIndicatorJudgeRiskFactorEntity::getExpression)
                    .eq(ExperimentIndicatorJudgeRiskFactorEntity::getExperimentJudgeRiskFactorId, judgeRiskFactorRequest.getExperimentJudgeRiskFactorId())
                    .eq(ExperimentIndicatorJudgeRiskFactorEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,不满足将flag变为false，只要有一个false,就说明失败
            flag.set(false);
        });
        return flag.get();
    }

    /**
     * @param
     * @return
     * @说明: 二级-无报告 获取判断得分
     * @关联表: indicatorJudgeRiskFactor
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 上午10:11:34
     */
    public BigDecimal getJudgeRiskFactorScore(List<ExperimentIndicatorJudgeRiskFactorRequest> judgeRiskFactorRequestList) {
        BigDecimal totalAmount = new BigDecimal(0);
        judgeRiskFactorRequestList.forEach(judgeRiskFactorRequest -> {
            //1、根据ID获取判断规则
            ExperimentIndicatorJudgeRiskFactorEntity entity = experimentIndicatorJudgeRiskFactorService.lambdaQuery()
                    .eq(ExperimentIndicatorJudgeRiskFactorEntity::getExperimentJudgeRiskFactorId, judgeRiskFactorRequest.getExperimentJudgeRiskFactorId())
                    .eq(ExperimentIndicatorJudgeRiskFactorEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,不满足将flag变为false，只要有一个false,就说明失败
            entity.getExpression();
            totalAmount.add(entity.getPoint());
        });
        return totalAmount;
    }

    /**
     * @param
     * @return
     * @说明: 直接判断 赋值
     * @关联表: experimentPersonHealthManagementGoal
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 下午17:24:34
     */
    public Boolean checkRangeMatchFormula(CreateIndicatorJudgeHealthManagementGoalRequest request) {
        Boolean flag = true;
        //1、根据直接判断分布式ID获取公式
        IndicatorJudgeHealthManagementGoalEntity entity = indicatorJudgeHealthManagementGoalService.lambdaQuery()
                .select(IndicatorJudgeHealthManagementGoalEntity::getExpression)
                .eq(IndicatorJudgeHealthManagementGoalEntity::getIndicatorJudgeHealthManagementGoalId, request.getIndicatorJudgeHealthManagementGoalId())
                .eq(IndicatorJudgeHealthManagementGoalEntity::getDeleted, false)
                .one();
        //2、todo 根据公式计算范围是否符合
        flag = false;
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 判断操作 保存
     * @关联表: operateOrgFunc、operateOrgFuncSnap
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月30日 下午17:12:34
     */
    @DSTransactional
    public Boolean saveExperimentJudgeOperate(List<OperateOrgFuncRequest> operateOrgFuncRequest, String accountId, String accountName) {
        //1、删除用户以前功能点信息
        List<OperateOrgFuncEntity> entityList = operateOrgFuncService.lambdaQuery()
                .select(OperateOrgFuncEntity::getId,OperateOrgFuncEntity::getDeleted)
                .eq(OperateOrgFuncEntity::getPeriods, operateOrgFuncRequest.get(0).getPeriods())
                .eq(OperateOrgFuncEntity::getIndicatorFuncId, operateOrgFuncRequest.get(0).getIndicatorFuncId())
                .eq(OperateOrgFuncEntity::getExperimentPersonId, operateOrgFuncRequest.get(0).getExperimentPersonId())
                .eq(OperateOrgFuncEntity::getAppId, operateOrgFuncRequest.get(0).getAppId())
                .eq(OperateOrgFuncEntity::getDeleted, false)
                .list();
        if (entityList != null && entityList.size() > 0) {
            List<OperateOrgFuncSnapEntity> snapList = new ArrayList<>();
            entityList.forEach(entity -> {
                entity.setDeleted(true);
                operateOrgFuncService.updateById(entity);
                OperateOrgFuncSnapEntity snapEntity = operateOrgFuncSnapService.lambdaQuery()
                        .eq(OperateOrgFuncSnapEntity::getOperateOrgFuncId, entity.getOperateOrgFuncId())
                        .eq(OperateOrgFuncSnapEntity::getAppId, operateOrgFuncRequest.get(0).getAppId())
                        .eq(OperateOrgFuncSnapEntity::getDeleted, false)
                        .one();
                snapList.add(snapEntity);

            });
            //1.1、删除用户以前的功能点快照
            if (snapList != null && snapList.size() > 0) {
                snapList.forEach(snap -> {
                    snap.setDeleted(true);
                });
                operateOrgFuncSnapService.updateBatchById(snapList);
            }
        }

        //2、重新插入数据
        List<OperateOrgFuncSnapEntity> snapList = new ArrayList<>();
        operateOrgFuncRequest.forEach(operateOrgFunc -> {
            OperateOrgFuncEntity entity = OperateOrgFuncEntity
                    .builder()
                    .appId(operateOrgFunc.getAppId())
                    .operateFlowId(operateOrgFunc.getOperateFlowId())
                    .indicatorFuncId(operateOrgFunc.getIndicatorFuncId())
                    .experimentInstanceId(operateOrgFunc.getExperimentInstanceId())
                    .experimentGroupId(operateOrgFunc.getExperimentGroupId())
                    .experimentPersonId(operateOrgFunc.getExperimentPersonId())
                    .experimentOrgId(operateOrgFunc.getExperimentOrgId())
                    .operateAccountId(accountId)
                    .operateAccountName(accountName)
                    .operateType(operateOrgFunc.getOperateType())
                    .periods(operateOrgFunc.getPeriods())
                    .score(operateOrgFunc.getScore())
                    .operateTime(new Date())
                    .build();
            operateOrgFuncService.save(entity);
            OperateOrgFuncSnapEntity snapEntity = OperateOrgFuncSnapEntity.builder()
                    .appId(operateOrgFunc.getAppId())
                    .operateOrgFuncId(entity.getOperateOrgFuncId())
                    .snapTime(new Date())
                    .inputJson(operateOrgFunc.getInputJson())
                    .build();
            snapList.add(snapEntity);
        });
        return operateOrgFuncSnapService.saveBatch(snapList);
    }
}