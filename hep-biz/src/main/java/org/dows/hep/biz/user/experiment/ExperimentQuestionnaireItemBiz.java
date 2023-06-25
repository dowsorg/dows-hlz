package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.dto.ExptQuestionnaireOptionDTO;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireItemRequest;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireItemResponse;
import org.dows.hep.entity.ExperimentQuestionnaireItemEntity;
import org.dows.hep.service.ExperimentQuestionnaireItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 14:21
 */

@AllArgsConstructor
@Service
public class ExperimentQuestionnaireItemBiz {
    private final ExperimentQuestionnaireItemService experimentQuestionnaireItemService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 列出知识答题
     * @date 2023/6/7 14:28
     */
    public List<ExperimentQuestionnaireItemResponse> listByQuestionnaireId(String questionnaireId) {
        List<ExperimentQuestionnaireItemEntity> entityList = experimentQuestionnaireItemService.lambdaQuery()
                .eq(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireId, questionnaireId)
                .list();
        return entityList.stream()
                .map(entity -> {
                    ExperimentQuestionnaireItemResponse resultItem = BeanUtil.copyProperties(entity, ExperimentQuestionnaireItemResponse.class);
                    List<ExptQuestionnaireOptionDTO> optionList = convertOptStr2OptionList(entity.getQuestionOptions());
                    List<String> results = convertResultStr2ResultList(entity.getQuestionResult());

                    resultItem.setQuestionOptionList(optionList);
                    resultItem.setQuestionResult(results);
                    return resultItem;
                })
                .toList();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 保存知识答题 Item
     * @date 2023/6/7 14:35
     */
    public Boolean updateBatch(List<ExperimentQuestionnaireItemRequest> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        List<ExperimentQuestionnaireItemEntity> itemEntityList = convertToFlatList(itemList);
        setId(itemEntityList);

        return experimentQuestionnaireItemService.updateBatchById(itemEntityList);
    }

    private List<ExperimentQuestionnaireItemEntity> convertToFlatList(List<ExperimentQuestionnaireItemRequest> itemList) {
        List<ExperimentQuestionnaireItemEntity> flatList = new ArrayList<>();
        itemList.forEach(node -> {
            flattenTree(node, flatList);
        });
        return flatList;
    }

    private void flattenTree(ExperimentQuestionnaireItemRequest node, List<ExperimentQuestionnaireItemEntity> flatList) {
        // 处理当前结点
        ExperimentQuestionnaireItemEntity itemEntity = ExperimentQuestionnaireItemEntity.builder()
                .experimentQuestionnaireItemId(node.getExperimentSchemeItemId())
                .questionResult(String.join(",", node.getQuestionResult()))
                .build();
        flatList.add(itemEntity);

        // 处理子节点
        for (ExperimentQuestionnaireItemRequest child : node.getChildren()) {
            flattenTree(child, flatList);
        }
    }

    private void setId(List<ExperimentQuestionnaireItemEntity> itemEntityList) {
        if (CollUtil.isEmpty(itemEntityList)) {
            return;
        }

        List<String> idList = itemEntityList.stream()
                .map(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireItemId)
                .toList();
        List<ExperimentQuestionnaireItemEntity> list = experimentQuestionnaireItemService.lambdaQuery()
                .in(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireItemId, idList)
                .list();
        Map<String, Long> collect = list.stream()
                .collect(Collectors.toMap(ExperimentQuestionnaireItemEntity::getExperimentQuestionnaireItemId, ExperimentQuestionnaireItemEntity::getId));
        itemEntityList.forEach(item -> {
            item.setId(collect.get(item.getExperimentQuestionnaireItemId()));
        });
    }

    private List<String> convertResultStr2ResultList(String questionResult) {
        if (StrUtil.isBlank(questionResult)) {
            return new ArrayList<>();
        }
        return List.of(questionResult.split(","));
    }

    private List<ExptQuestionnaireOptionDTO> convertOptStr2OptionList(String questionOptions) {
        if (StrUtil.isBlank(questionOptions)) {
            return new ArrayList<>();
        }
        return JSONUtil.toList(questionOptions, ExptQuestionnaireOptionDTO.class);
    }
}
