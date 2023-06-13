package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(name = "ExperimentAllotSchemeRequest 对象", title = "分配方案")
public class ExperimentAllotSchemeRequest {
    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "分配列表")
    private List<ParticipatorWithScheme> allotList;

    @Data
    @NoArgsConstructor
    public static class ParticipatorWithScheme {
        @Schema(title = "实验参与者ID")
        private String experimentParticipatorId;

        @Schema(title = "实验方案设计ItemId")
        private String experimentSchemeIds;
    }
}
