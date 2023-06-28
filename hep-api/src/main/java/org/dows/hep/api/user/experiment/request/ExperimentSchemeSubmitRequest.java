package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 提交方案设计Request
 * @date 2023/6/28 9:31
 **/
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentSchemeSubmitRequest 对象", title = "提交实验方案设计")
public class ExperimentSchemeSubmitRequest {
    @Schema(title = "实验实例ID")
    @NotBlank(message = "实验实例ID不能为空")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    @NotBlank(message = "实验小组ID不能为空")
    private String experimentGroupId;

    @Schema(title = "提交者账号ID")
    @JsonIgnore
    private String accountId;
}
