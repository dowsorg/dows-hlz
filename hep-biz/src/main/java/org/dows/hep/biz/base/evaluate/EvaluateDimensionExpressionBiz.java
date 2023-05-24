package org.dows.hep.biz.base.evaluate;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.EvaluateESCEnum;
import org.dows.hep.api.base.evaluate.request.EvaluateDimensionExpressionRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateDimensionExpressionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.biz.base.question.QuestionSectionDimensionBiz;
import org.dows.hep.entity.EvaluateDimensionExpressionEntity;
import org.dows.hep.service.EvaluateDimensionExpressionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author lait.zhang
 * @description project descr:评估:评估维度公式
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class EvaluateDimensionExpressionBiz {
    private final EvaluateBaseBiz baseBiz;
    private final EvaluateDimensionExpressionService expressionService;
    private final QuestionSectionDimensionBiz questionSectionDimensionBiz;

    /**
     * @param
     * @return
     * @说明: 根据问题集ID维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<QuestionSectionDimensionResponse> listSectionDimension(String questionSectionId) {
        if (StrUtil.isBlank(questionSectionId)) {
            return new ArrayList<>();
        }

        return questionSectionDimensionBiz.listQuestionSectionDimension(questionSectionId);
    }

    /**
     * @param
     * @return
     * @说明: 创建评估维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public boolean saveOrUpdDimensionExpression(List<EvaluateDimensionExpressionRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        List<EvaluateDimensionExpressionEntity> entityList = convertRequest2Entity(requests);
        return expressionService.saveOrUpdateBatch(entityList);
    }

    /**
     * @param
     * @return
     * @说明: 列出问卷下评估维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<EvaluateDimensionExpressionResponse> listByQuestionnaireId(String evaluateQuestionnaireId) {
        if (BeanUtil.isEmpty(evaluateQuestionnaireId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<EvaluateDimensionExpressionEntity> queryWrapper = new LambdaQueryWrapper<EvaluateDimensionExpressionEntity>()
                .eq(EvaluateDimensionExpressionEntity::getEvaluateQuestionnaireId, evaluateQuestionnaireId);
        List<EvaluateDimensionExpressionEntity> list = expressionService.list(queryWrapper);

        return BeanUtil.copyToList(list, EvaluateDimensionExpressionResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 获取评估维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public EvaluateDimensionExpressionEntity getById(String evaluateDimensionExpressionId) {
        LambdaQueryWrapper<EvaluateDimensionExpressionEntity> queryWrapper = new LambdaQueryWrapper<EvaluateDimensionExpressionEntity>()
                .eq(EvaluateDimensionExpressionEntity::getEvaluateDimensionExpressionId, evaluateDimensionExpressionId);
        return expressionService.getOne(queryWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 获取评估维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public EvaluateDimensionExpressionResponse getEvaluateDimensionExpression(String evaluateDimensionExpressionId) {
        if (StrUtil.isBlank(evaluateDimensionExpressionId)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        EvaluateDimensionExpressionEntity entity = getById(evaluateDimensionExpressionId);
        return BeanUtil.copyProperties(entity, EvaluateDimensionExpressionResponse.class);
    }

    /**
     * @param
     * @return
     * @说明: 删除评估维度公式
     * @关联表:
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean deleteEvaluateDimensionExpression(List<String> evaluateDimensionExpressionIds) {
        if (evaluateDimensionExpressionIds == null || evaluateDimensionExpressionIds.isEmpty()) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        LambdaQueryWrapper<EvaluateDimensionExpressionEntity> remWrapper = new LambdaQueryWrapper<EvaluateDimensionExpressionEntity>()
                .in(EvaluateDimensionExpressionEntity::getEvaluateDimensionExpressionId, evaluateDimensionExpressionIds);
        return expressionService.remove(remWrapper);
    }

    private List<EvaluateDimensionExpressionEntity> convertRequest2Entity(List<EvaluateDimensionExpressionRequest> requests) {
        if (BeanUtil.isEmpty(requests)) {
            throw new BizException(EvaluateESCEnum.PARAMS_NON_NULL);
        }

        ArrayList<EvaluateDimensionExpressionEntity> result = new ArrayList<>();
        requests.forEach(request -> {
            EvaluateDimensionExpressionEntity resultEntity = EvaluateDimensionExpressionEntity.builder()
                    .evaluateDimensionExpressionId(request.getEvaluateDimensionExpressionId())
                    .evaluateQuestionnaireId(request.getEvaluateQuestionnaireId())
                    .dimensionId(request.getDimensionId())
                    .expression(request.getExpression())
                    .build();

            String uniqueId = resultEntity.getEvaluateDimensionExpressionId();
            if (StrUtil.isBlank(uniqueId)) {
                resultEntity.setEvaluateDimensionExpressionId(baseBiz.getIdStr());
            } else {
                EvaluateDimensionExpressionEntity entity = getById(uniqueId);
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