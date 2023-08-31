package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 13:47
 */
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentSchemeRequest 对象", title = "实验方案设计")
public class ExperimentSchemeRequest {
    @NotBlank
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "视频答案-如果有的话")
    private String videoAnswer;

    @NotNull
    @Valid
    @Schema(title = "方案设计试卷")
    private List<ExperimentSchemeItemRequest> itemList;
}
