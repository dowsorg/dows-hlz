package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeItemRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.entity.ExperimentSchemeItemEntity;
import org.dows.hep.service.ExperimentSchemeItemService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 11:49
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeItemBiz {

    private final ExperimentSchemeItemService experimentSchemeItemService;

    /**
     * @author fhb
     * @description 列出方案设计 item
     * @date 2023/6/7 13:51
     * @param
     * @return
     */
    public List<ExperimentSchemeItemResponse> listBySchemeId(String schemeId) {
        List<ExperimentSchemeItemEntity> entityList = experimentSchemeItemService.lambdaQuery()
                .eq(ExperimentSchemeItemEntity::getExperimentSchemeId, schemeId)
                .list();
        return BeanUtil.copyToList(entityList, ExperimentSchemeItemResponse.class);
    }

    /**
     * @author fhb
     * @description 保存方案设计 Item
     * @date 2023/6/7 13:51
     * @param
     * @return
     */
    public Boolean updateBatch(List<ExperimentSchemeItemRequest> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        List<ExperimentSchemeItemEntity> itemEntityList = convertToFlatList(itemList);
        setId(itemEntityList);

        return experimentSchemeItemService.updateBatchById(itemEntityList);
    }

    private List<ExperimentSchemeItemEntity> convertToFlatList(List<ExperimentSchemeItemRequest> itemList) {
        List<ExperimentSchemeItemEntity> flatList = new ArrayList<>();
        itemList.forEach(node -> {
            flattenTree(node, flatList);
        });
        return flatList;
    }

    private void flattenTree(ExperimentSchemeItemRequest node, List<ExperimentSchemeItemEntity> flatList) {
        // 处理当前结点
        ExperimentSchemeItemEntity itemEntity = ExperimentSchemeItemEntity.builder()
                .experimentSchemeItemId(node.getExperimentSchemeItemId())
                .accountId(node.getAccountId())
                .questionResult(node.getQuestionResult())
                .build();
        flatList.add(itemEntity);

        // 处理子节点
        for (ExperimentSchemeItemRequest child : node.getChildren()) {
            flattenTree(child, flatList);
        }
    }

    private void setId(List<ExperimentSchemeItemEntity> itemEntityList) {
        if (CollUtil.isEmpty(itemEntityList)) {
            return;
        }

        List<String> idList = itemEntityList.stream()
                .map(ExperimentSchemeItemEntity::getExperimentSchemeItemId)
                .toList();
        List<ExperimentSchemeItemEntity> list = experimentSchemeItemService.lambdaQuery()
                .in(ExperimentSchemeItemEntity::getExperimentSchemeItemId, idList)
                .list();
        Map<String, Long> collect = list.stream()
                .collect(Collectors.toMap(ExperimentSchemeItemEntity::getExperimentSchemeItemId, ExperimentSchemeItemEntity::getId));
        itemEntityList.forEach(item -> {
            item.setId(collect.get(item.getExperimentSchemeItemId()));
        });
    }
}
