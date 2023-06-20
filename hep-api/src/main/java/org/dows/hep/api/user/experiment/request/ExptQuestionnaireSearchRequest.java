package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptQuestionnaireSearchRequest 对象", title = "实验知识答题查询")
public class ExptQuestionnaireSearchRequest {
    @NotBlank
    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @NotBlank
    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @NotBlank
    @Schema(title = "实验组ID")
    private String experimentGroupId;

    @JsonIgnore
    @Schema(title = "实验账号ID")
    private String experimentAccountId;
}
