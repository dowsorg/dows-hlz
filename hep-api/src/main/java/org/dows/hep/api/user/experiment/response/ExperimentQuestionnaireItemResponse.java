package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 14:23
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExperimentQuestionnaireItemResponse 对象", title = "实验知识答题")
public class ExperimentQuestionnaireItemResponse {
    @Schema(title = "item id")
    private String experimentQuestionnaireItemId;

    @Schema(title = "问题类别")
    private String questionCateg;

    @Schema(title = "问题类型")
    private String questionType;

    @Schema(title = "问题题目")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "问题选项")
    private String questionOptions;

    @Schema(title = "问题答案")
    private String questionResult;

    @Schema(title = "子")
    private List<ExperimentQuestionnaireItemResponse> children = new ArrayList<>();

    @Schema(title = "item pid")
    @JsonIgnore
    private String experimentQuestionnaireItemPid;

    public void addChild(ExperimentQuestionnaireItemResponse child) {
        this.children.add(child);
    }

}
