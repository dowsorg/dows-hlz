package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExperimentAllotQuestionnaireRequest 对象", title = "分配知识答题")
public class ExptQuestionnaireAllotRequest {
    @NotBlank
    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @NotBlank
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "分配列表")
    @NotNull
    private List<ParticipatorWithQuestionnaire> allotList;

    @Data
    @NoArgsConstructor
    public static class ParticipatorWithQuestionnaire {
        @NotBlank
        @Schema(title = "参赛者账号ID")
        private String accountId;

        @NotNull
        @Schema(title = "实验机构ID")
        private List<String> experimentOrgIds;
    }
}
