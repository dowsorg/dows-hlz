package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "ExperimentAllotSchemeRequest 对象", title = "分配方案")
public class ExperimentAllotSchemeRequest {
    @NotBlank
    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @NotBlank
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "分配列表")
    @NotNull
    private List<ParticipatorWithScheme> allotList;

    @Data
    @NoArgsConstructor
    public static class ParticipatorWithScheme {
        @NotBlank
        @Schema(title = "参赛者账号ID")
        private String accountId;

        @NotNull
        @Schema(title = "实验方案设计ItemId")
        private List<String> experimentSchemeIds;
    }
}
