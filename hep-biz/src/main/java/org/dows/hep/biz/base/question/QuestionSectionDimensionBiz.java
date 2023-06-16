package org.dows.hep.biz.base.question;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.QuestionESCEnum;
import org.dows.hep.api.base.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.entity.QuestionSectionDimensionEntity;
import org.dows.hep.service.QuestionSectionDimensionService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:问题:问题集-维度
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@Service
public class QuestionSectionDimensionBiz{
    private final QuestionBaseBiz baseBiz;
    private final QuestionSectionDimensionService questionSectionDimensionService;

    /**
     * @param
     * @return
     * @说明: 新增和更新问题集维度
     * @关联表: QuestionSection, QuestionSectionDimension
     * @工时: 4H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean batchSaveOrUpdQSDimension(List<QuestionSectionDimensionRequest> questionSectionDimensionList, String questionSectionId) {
        if (questionSectionDimensionList == null || questionSectionDimensionList.isEmpty()) {
            return Boolean.FALSE;
        }

        List<QuestionSectionDimensionEntity> entityList = convertRequest2Entity(questionSectionDimensionList, questionSectionId);
        return questionSectionDimensionService.saveOrUpdateBatch(entityList);
    }

    /**
    * @param
    * @return
    * @说明: 获取问题集所有维度
    * @关联表: QuestionSection,QuestionSectionDimension
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<QuestionSectionDimensionResponse> listQuestionSectionDimension(String questionSectionId ) {
        if (StrUtil.isBlank(questionSectionId)) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .eq(QuestionSectionDimensionEntity::getQuestionSectionId, questionSectionId);
        List<QuestionSectionDimensionEntity> entityList = questionSectionDimensionService.list(queryWrapper);
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(item -> BeanUtil.copyProperties(item, QuestionSectionDimensionResponse.class))
                .toList();
    }

    /**
     * @param
     * @return
     * @说明: 获取问题集所有维度
     * @关联表: QuestionSection, QuestionSectionDimension
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<QuestionSectionDimensionEntity> listByIds(List<String> dimensionIds) {
        if (Objects.isNull(dimensionIds) || dimensionIds.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<QuestionSectionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .in(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, dimensionIds);
        return questionSectionDimensionService.list(queryWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 删除问题集维度
     * @关联表: QuestionSection, QuestionSectionDimension
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean delQuestionSectionDimension(List<String> questionSectionDimensionIds) {
        if (questionSectionDimensionIds == null || questionSectionDimensionIds.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<QuestionSectionDimensionEntity> remWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .in(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, questionSectionDimensionIds);
        return questionSectionDimensionService.remove(remWrapper);
    }

    public QuestionSectionDimensionEntity getById(String uniqueId) {
        LambdaQueryWrapper<QuestionSectionDimensionEntity> queryWrapper = new LambdaQueryWrapper<QuestionSectionDimensionEntity>()
                .eq(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, uniqueId);
        return questionSectionDimensionService.getOne(queryWrapper);
    }

    private List<QuestionSectionDimensionEntity> convertRequest2Entity(List<QuestionSectionDimensionRequest> request, String questionSectionId) {
        if (Objects.isNull(request) || request.isEmpty() || StrUtil.isBlank(questionSectionId)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }
        // check score
        checkDimensionScore(request);

        List<QuestionSectionDimensionEntity> result = new ArrayList<>();
        request.forEach(item -> {
            QuestionSectionDimensionEntity instance = QuestionSectionDimensionEntity.builder()
                    .questionSectionId(questionSectionId)
                    .questionSectionDimensionId(item.getQuestionSectionDimensionId())
                    .dimensionName(item.getDimensionName())
                    .dimensionContent(item.getDimensionContent())
                    .minScore(item.getMinScore())
                    .maxScore(item.getMaxScore())
                    .build();
            result.add(instance);
        });

        // addList
        List<QuestionSectionDimensionEntity> addList = result.stream()
                .filter(item -> StrUtil.isBlank(item.getQuestionSectionDimensionId()))
                .toList();
        if (!addList.isEmpty()) {
            addList.forEach(item -> item.setQuestionSectionDimensionId(baseBiz.getIdStr()));
        }

        // updList
        List<QuestionSectionDimensionEntity> updList = result.stream()
                .filter(item -> !StrUtil.isBlank(item.getQuestionSectionDimensionId()))
                .toList();
        if (!updList.isEmpty()) {
            List<String> ids = updList.stream().map(QuestionSectionDimensionEntity::getQuestionSectionDimensionId).toList();
            List<QuestionSectionDimensionEntity> entityList = listByIds(ids);
            if (!entityList.isEmpty()) {
                Map<String, Long> collect = entityList.stream().collect(Collectors.toMap(QuestionSectionDimensionEntity::getQuestionSectionDimensionId, QuestionSectionDimensionEntity::getId));
                updList.forEach(item -> {
                    item.setId(collect.get(item.getQuestionSectionDimensionId()));
                });
            }
        }

        return result;
    }

    private void checkDimensionScore(List<QuestionSectionDimensionRequest> requests) {
        if (CollUtil.isEmpty(requests)) {
            return;
        }

        // 空则设默认值
        requests.forEach(request -> {
            request.setMinScore(request.getMinScore() == null ? 0.0f : request.getMinScore());
            request.setMaxScore(request.getMaxScore() == null ? 0.0f : request.getMaxScore());
        });

        // 分组
        Map<String, List<QuestionSectionDimensionRequest>> collect = requests.stream().collect(Collectors.groupingBy(QuestionSectionDimensionRequest::getDimensionName));

        // 判别没有交集
        collect.forEach((name, contentList) -> {
            float[][] scores = new float[contentList.size()][];
            for (int i = 0; i < contentList.size(); i++) {
                float minScore = contentList.get(i).getMinScore();
                float maxScore = contentList.get(i).getMaxScore();
                float[] arr = new float[]{minScore, maxScore};
                scores[i] = arr;
            }
            boolean hasIntersection = hasIntersection(scores);
            if (hasIntersection) {
                throw new BizException(QuestionESCEnum.QUESTION_SECTION_DIMENSION_SCORE_RANGE_ERROR);
            }
        });
    }

    // 是否有交集
    public static boolean hasIntersection(float[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return false;
        }

        // 按照起始点进行升序排序
        Arrays.sort(intervals, Comparator.comparingDouble(a -> a[0]));

        for (int i = 1; i < intervals.length; i++) {
            float[] prevInterval = intervals[i - 1];
            float[] currInterval = intervals[i];

            // 判断是否有交集
            if (currInterval[0] <= prevInterval[1]) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
//        float[][] intervals = {{0.0f, 2.0f}, {1.2f, 5.3f}, {3.4f, 8.9f}};
        float[][] intervals = {{0.0f, 2.0f}, {3.4f, 8.9f}};
        boolean hasIntersection = hasIntersection(intervals);
        System.out.println("Has intersection: " + hasIntersection);
    }
}