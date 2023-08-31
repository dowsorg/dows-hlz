package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 18:54
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Schema(name = "ExperimentSchemeItemRequest 对象", title = "实验方案设计Item")
public class ExperimentSchemeItemRequest {
    @NotBlank
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeItemId;

    @NotBlank
    @Schema(title = "账号ID")
    private String accountId;

    @Schema(title = "作答答案")
    @Size(min = 0, max = 10000, message = "答案长度应为0-10000")
    private String questionResult;

    @Schema(title = "子")
    private List<ExperimentSchemeItemRequest> children;
}
