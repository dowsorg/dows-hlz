package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:54
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExperimentSchemeResponse 对象", title = "实验方案设计")
public class ExperimentSchemeItemResponse {

    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "问题")
    private Question question;

    @Schema(title = "是否可以编辑")
    private Boolean canEdit;

    @Data
    @ToString
    @Builder
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        @Schema(title = "题目ID")
        private String questionId;

        @Schema(title = "问题标题")
        private String questionTitle;

        @Schema(title = "子题目")
        private List<Question> children;
    }

}
