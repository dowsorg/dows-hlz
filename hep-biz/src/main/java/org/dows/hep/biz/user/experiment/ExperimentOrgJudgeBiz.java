package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeHealthProblemRequest;
import org.dows.hep.api.base.indicator.request.CreateIndicatorJudgeRiskFactorRequest;
import org.dows.hep.api.base.indicator.request.ExperimentPersonHealthProblemRequest;
import org.dows.hep.api.base.indicator.response.ExperimentPersonHealthProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthGuidanceResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeHealthProblemResponse;
import org.dows.hep.api.base.indicator.response.IndicatorJudgeRiskFactorResponse;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.ExperimentPersonHealthProblemService;
import org.dows.hep.service.ExperimentPersonPropertyService;
import org.dows.hep.service.IndicatorJudgeHealthGuidanceService;
import org.dows.hep.service.IndicatorJudgeHealthProblemService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
* @description project descr:实验:机构操作-判断指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class ExperimentOrgJudgeBiz{
    private final org.dows.hep.service.IndicatorJudgeRiskFactorService indicatorJudgeRiskFactorService;
    private final IndicatorJudgeHealthGuidanceService indicatorJudgeHealthGuidanceService;
    private final IndicatorJudgeHealthProblemService indicatorJudgeHealthProblemService;
    private final ExperimentPersonPropertyService experimentPersonPropertyService;
    private final ExperimentPersonHealthProblemService experimentPersonHealthProblemService;
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导：获取问题列表（含分类）
    * @关联表: indicator_judge_health_problem,indicator_judge_health_guidance,OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgJudgeItemsResponse> listOrgJudgeItems(FindOrgJudgeItemsRequest findOrgJudgeItems ) {
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
    public List<OrgJudgeCategResponse> listOrgJudgeCategs(FindOrgJudgeCategsRequest findOrgJudgeCategs ) {
        return new ArrayList<OrgJudgeCategResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导+疾病问题：获取最新保存列表
    * @关联表: indicator_judge_health_problem,indicator_judge_health_guidance,indicator_judge_disease_problem，OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<OrgJudgedItemsResponse> listOrgJudgedItems(FindOrgJudgedItemsRequest findOrgJudgedItems ) {
        return new ArrayList<OrgJudgedItemsResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 健康问题+健康指导+疾病问题：保存
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgJudgeResponse saveOrgJudge(SaveOrgJudgeRequest saveOrgJudge ) {
        return new SaveOrgJudgeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 健管目标：获取健管目标列表
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgJudgeGoalsResponse listOrgJudgeGoals(FindOrgJudgeGoalsRequest findOrgJudgeGoals ) {
        return new OrgJudgeGoalsResponse();
    }
    /**
    * @param
    * @return
    * @说明: 健管目标：保存，包含是否购买保险
    * @关联表: OperateOrgFunc,OperateOrgFuncSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SaveOrgJudgeGoalsResponse saveOrgJudgeGoals(SaveOrgJudgeGoalsRequest saveOrgJudgeGoals ) {
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
    public Map<String,List<IndicatorJudgeRiskFactorResponse>> getIndicatorJudgeRiskFactor(String indicatorFuncId) {
        //1、根据指标功能ID获取所有的分类
        List<IndicatorJudgeRiskFactorEntity> entityList = indicatorJudgeRiskFactorService.lambdaQuery()
                .eq(IndicatorJudgeRiskFactorEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(IndicatorJudgeRiskFactorEntity::getStatus, true)
                .list();
        List<IndicatorJudgeRiskFactorResponse> responseList = new ArrayList<>();
        if(entityList != null && entityList.size() > 0){
            entityList.forEach(entity->{
                IndicatorJudgeRiskFactorResponse response = IndicatorJudgeRiskFactorResponse
                        .builder()
                        .id(entity.getId())
                        .indicatorJudgeRiskFactorId(entity.getIndicatorJudgeRiskFactorId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        //2、根据分类ID分组
        Map<String,List<IndicatorJudgeRiskFactorResponse>> categoryList = responseList.stream().collect(Collectors.groupingBy(IndicatorJudgeRiskFactorResponse::getIndicatorCategoryId));
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
    public Map<String, List<IndicatorJudgeHealthGuidanceResponse>> getIndicatorJudgeHealthGuidance(String indicatorFuncId) {
        //1、根据指标功能ID获取所有的分类
        List<IndicatorJudgeHealthGuidanceEntity> entityList = indicatorJudgeHealthGuidanceService.lambdaQuery()
                .eq(IndicatorJudgeHealthGuidanceEntity::getIndicatorFuncId, indicatorFuncId)
                .eq(IndicatorJudgeHealthGuidanceEntity::getStatus, true)
                .list();
        List<IndicatorJudgeHealthGuidanceResponse> responseList = new ArrayList<>();
        if(entityList != null && entityList.size() > 0){
            entityList.forEach(entity->{
                IndicatorJudgeHealthGuidanceResponse response = IndicatorJudgeHealthGuidanceResponse
                        .builder()
                        .id(entity.getId())
                        .indicatorJudgeHealthGuidanceId(entity.getIndicatorJudgeHealthGuidanceId())
                        .name(entity.getName())
                        .indicatorCategoryId(entity.getIndicatorCategoryId())
                        .build();
                responseList.add(response);
            });
        }
        //2、根据分类ID分组
        Map<String,List<IndicatorJudgeHealthGuidanceResponse>> categoryList = responseList.stream().collect(Collectors.groupingBy(IndicatorJudgeHealthGuidanceResponse::getIndicatorCategoryId));
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
    public List<IndicatorJudgeHealthProblemResponse> getIndicatorJudgeHealthProblemByCategoryId(String indicatoryCategoryId) {
        //1、根据指标分类ID获取所有符合条件的数据
        List<IndicatorJudgeHealthProblemEntity> entityList = indicatorJudgeHealthProblemService.lambdaQuery()
                .eq(IndicatorJudgeHealthProblemEntity::getIndicatorCategoryId, indicatoryCategoryId)
                .eq(IndicatorJudgeHealthProblemEntity::getStatus, true)
                .list();
        List<IndicatorJudgeHealthProblemResponse> responseList = new ArrayList<>();
        if(entityList != null && entityList.size() > 0) {
            entityList.forEach(entity -> {
                IndicatorJudgeHealthProblemResponse response = IndicatorJudgeHealthProblemResponse
                        .builder()
                        .id(entity.getId())
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
     * @说明: 是否购买保险
     * @关联表: experimentPersonProperty
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月26日 下午16:11:34
     */
    @DSTransactional
    public Boolean isPurchaseInsure(String isPurchase,String experimentPersonId) {
        LambdaUpdateWrapper<ExperimentPersonPropertyEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentPersonPropertyEntity>()
                .eq(ExperimentPersonPropertyEntity::getExperimentPersonId, experimentPersonId)
                .eq(ExperimentPersonPropertyEntity::getDeleted,false)
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
    public Boolean isIndicatorJudgeRiskFactor(List<CreateIndicatorJudgeRiskFactorRequest> judgeRiskFactorRequestList) {
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        judgeRiskFactorRequestList.forEach(judgeRiskFactorRequest -> {
            //1、根据ID获取判断规则
            IndicatorJudgeRiskFactorEntity entity = indicatorJudgeRiskFactorService.lambdaQuery()
                    .eq(IndicatorJudgeRiskFactorEntity::getIndicatorJudgeRiskFactorId, judgeRiskFactorRequest.getIndicatorJudgeRiskFactorId())
                    .eq(IndicatorJudgeRiskFactorEntity::getStatus, true)
                    .one();
            //todo、根据判断规则判断是否满足条件,不满足将flag变为false，只要有一个false,就说明失败
            entity.getExpression();
            flag.set(false);
        });
        return flag.get();
    }

    /**
     * @param
     * @return
     * @说明: 三级-无报告 保存操作
     * @关联表: experimentPersonHealthProblem
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 下午16:11:34
     */
    @DSTransactional
    public Boolean saveExperimentIndicatorJudgeHealthProblem(List<CreateIndicatorJudgeHealthProblemRequest> judgeHealthProblemRequestList) {
        List<ExperimentPersonHealthProblemEntity> modelList = new ArrayList<>();
        judgeHealthProblemRequestList.forEach(judgeHealthProblemRequest->{
            ExperimentPersonHealthProblemEntity model = ExperimentPersonHealthProblemEntity
                    .builder()
                    .indicatorJudgeHealthProblemId(judgeHealthProblemRequest.getIndicatorJudgeHealthProblemId())
                    .experimentPersonId(judgeHealthProblemRequest.getExperimentPersonId())
                    .name(judgeHealthProblemRequest.getName())
                    .healthProbleamCategoryName(judgeHealthProblemRequest.getHealthProbleamCategoryName())
                    .build();
            modelList.add(model);
        });
        return experimentPersonHealthProblemService.saveBatch(modelList);
    }

    /**
     * @param
     * @return
     * @说明: 三级-无报告 获取列表
     * @关联表: experimentPersonHealthProblem
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 下午14:53:34
     */
    public IPage<ExperimentPersonHealthProblemResponse> pageExperimentIndicatorJudgeHealthProblem(ExperimentPersonHealthProblemRequest experimentPersonHealthProblemRequest) {
        Page<ExperimentPersonHealthProblemEntity> page = new Page<>(experimentPersonHealthProblemRequest.getPageNo(), experimentPersonHealthProblemRequest.getPageSize());
        IPage<ExperimentPersonHealthProblemEntity> pageResult = experimentPersonHealthProblemService.lambdaQuery()
                .eq(ExperimentPersonHealthProblemEntity::getExperimentPersonId, experimentPersonHealthProblemRequest.getExperimentPersonId())
                .eq(ExperimentPersonHealthProblemEntity::getDeleted, false)
                .page(page);
        // 复制
        IPage<ExperimentPersonHealthProblemResponse> voPage = new Page<>();
        BeanUtils.copyProperties(pageResult, voPage, new String[]{"records"});
        List<ExperimentPersonHealthProblemResponse> responseList = new ArrayList<>();
        for(ExperimentPersonHealthProblemEntity entity : pageResult.getRecords()){
            ExperimentPersonHealthProblemResponse person = new ExperimentPersonHealthProblemResponse();
            BeanUtil.copyProperties(entity,person);
            person.setId(entity.getId());
            responseList.add(person);
        }
        voPage.setRecords(responseList);
        return voPage;
    }

    /**
     * @param
     * @return
     * @说明: 三级-无报告 删除数据
     * @关联表: experimentPersonHealthProblem
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月29日 下午15:41:34
     */
    @DSTransactional
    public Boolean delExperimentIndicatorJudgeHealthProblem(String indicatorJudgeHealthProblemId, String experimentPersonId) {
        LambdaUpdateWrapper<ExperimentPersonHealthProblemEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentPersonHealthProblemEntity>()
                .eq(ExperimentPersonHealthProblemEntity::getIndicatorJudgeHealthProblemId, indicatorJudgeHealthProblemId)
                .eq(ExperimentPersonHealthProblemEntity::getExperimentPersonId,experimentPersonId)
                .eq(ExperimentPersonHealthProblemEntity::getDeleted,false)
                .set(ExperimentPersonHealthProblemEntity::getDeleted, true);
        return experimentPersonHealthProblemService.update(updateWrapper);
    }
}