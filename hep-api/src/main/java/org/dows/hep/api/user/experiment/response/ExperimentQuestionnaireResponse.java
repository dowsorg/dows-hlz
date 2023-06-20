package org.dows.hep.api.user.experiment.response;

import cn.hutool.core.collection.CollUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 20:45
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentQuestionnaireResponse 对象", title = "实验知识答题")
public class ExperimentQuestionnaireResponse {
    @Schema(title = "实验知识答题ID")
    private String experimentQuestionnaireId;

    @Schema(title = "案例问卷名")
    private String questionnaireName;

    @Schema(title = "知识答题试卷-全部item")
    private List<ExperimentQuestionnaireItemResponse> itemList;

    @Schema(title = "知识答题试卷-按类别分组")
    private List<ExptCategQuestionnaireItem> categItemList;

    @Data
    public static class ExptCategQuestionnaireItem {

        @Schema(title = "题目类别")
        private String categName;

        @Schema(title = "该类别下题目集合")
        private List<ExperimentQuestionnaireItemResponse> itemList;
    }

    public static List<ExptCategQuestionnaireItem> convertItemList2CategItemList(List<ExperimentQuestionnaireItemResponse> itemList) {
        if (CollUtil.isEmpty(itemList)) {
            return new ArrayList<>();
        }

        Map<String, List<ExperimentQuestionnaireItemResponse>> collect = itemList.stream()
                .collect(Collectors.groupingBy(ExperimentQuestionnaireItemResponse::getQuestionCateg));
        if (CollUtil.isEmpty(collect)) {
            return new ArrayList<>();
        }

        List<ExptCategQuestionnaireItem> result = new ArrayList<>();
        collect.forEach((k, v) -> {
            ExptCategQuestionnaireItem item = new ExptCategQuestionnaireItem();
            item.setCategName(k);
            item.setItemList(v);
            result.add(item);
        });

        return result;
    }
}
