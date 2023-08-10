package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description
 * @date 2023/6/26 11:44
 **/
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentScheme1Request 对象", title = "实验方案设计")
public class ExperimentScheme1Request {
    @NotBlank
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeItemId;

    @Schema(title = "作答答案")
    private String questionResult;

    @Schema(title = "视频答案-如果有的话")
    private String videoAnswer;
}
