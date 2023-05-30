package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.EvaluateESCEnum;
import org.dows.hep.api.base.evaluate.request.EvaluateReportRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateReportManagementResponse;
import org.dows.hep.entity.EvaluateReportManagementEntity;
import org.dows.hep.service.EvaluateReportManagementService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
* @description project descr:评估:评估报告管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@AllArgsConstructor
@Service
public class EvaluateReportBiz {
    private final EvaluateBaseBiz baseBiz;
    private final EvaluateReportManagementService reportService;
    /**
    * @param
    * @return
    * @说明: 新增或更新评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveOrUpdEvaluateReport(List<EvaluateReportRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        List<EvaluateReportManagementEntity> entityList = convertRequest2Entity(requests);
        return reportService.saveOrUpdateBatch(entityList);
    }

    /**
     * @param
     * @return
     * @说明: 获取问卷下评估报告
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<EvaluateReportManagementResponse> listByQuestionnaireId(String evaluateQuestionnaireId) {
        if (StrUtil.isEmpty(evaluateQuestionnaireId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<EvaluateReportManagementEntity> queryWrapper = new LambdaQueryWrapper<EvaluateReportManagementEntity>()
                .eq(EvaluateReportManagementEntity::getEvaluateQuestionnaireId, evaluateQuestionnaireId);
        List<EvaluateReportManagementEntity> list = reportService.list(queryWrapper);

        return BeanUtil.copyToList(list, EvaluateReportManagementResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 获取评估报告管理
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public EvaluateReportManagementEntity getById(String uniqueId) {
        LambdaQueryWrapper<EvaluateReportManagementEntity> queryWrapper = new LambdaQueryWrapper<EvaluateReportManagementEntity>()
                .eq(EvaluateReportManagementEntity::getEvaluateReportManagementId, uniqueId);
        return reportService.getOne(queryWrapper);
    }

    /**
    * @param
    * @return
    * @说明: 获取评估报告管理
    * @关联表: 
    * @工时: 4H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public EvaluateReportManagementResponse getEvaluateReportManagement(String evaluateReportManagementId ) {
        if (StrUtil.isBlank(evaluateReportManagementId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        EvaluateReportManagementEntity entity = getById(evaluateReportManagementId);
        return BeanUtil.copyProperties(entity, EvaluateReportManagementResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 删除评估报告管理
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean deleteEvaluateReportManagement(List<String> evaluateReportManagementIds ) {
        if (CollUtil.isEmpty(evaluateReportManagementIds)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<EvaluateReportManagementEntity> remWrapper = new LambdaQueryWrapper<EvaluateReportManagementEntity>()
                .in(EvaluateReportManagementEntity::getEvaluateReportManagementId, evaluateReportManagementIds);
        return reportService.remove(remWrapper);
    }

    private List<EvaluateReportManagementEntity> convertRequest2Entity(List<EvaluateReportRequest> requests) {
        if (CollUtil.isEmpty(requests)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        ArrayList<EvaluateReportManagementEntity> result = new ArrayList<>();
        requests.forEach(request -> {
            EvaluateReportManagementEntity resultEntity = EvaluateReportManagementEntity.builder()
                    .evaluateReportManagementId(request.getEvaluateReportManagementId())
                    .evaluateQuestionnaireId(request.getEvaluateQuestionnaireId())
                    .reportName(request.getReportName())
                    .reportDescr(request.getReportDescr())
                    .assessmentResult(request.getAssessmentResult())
                    .suggestion(request.getSuggestion())
                    .minScore(request.getMinScore())
                    .maxScore(request.getMaxScore())
                    .build();

            String uniqueId = resultEntity.getEvaluateReportManagementId();
            if (StrUtil.isBlank(uniqueId)) {
                resultEntity.setEvaluateReportManagementId(baseBiz.getIdStr());
            } else {
                EvaluateReportManagementEntity entity = getById(uniqueId);
                if (BeanUtil.isEmpty(entity)) {
                    throw new BizException(EvaluateESCEnum.DATA_NULL);
                }
                resultEntity.setId(entity.getId());
            }

            result.add(resultEntity);
        });

        return result;
    }
}