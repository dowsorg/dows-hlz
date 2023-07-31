package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.dows.hep.api.BasePageRequest;

/**
 * @author fhb
 * @version 1.0
 * @description 实验小组报告查询请求
 * @date 2023/7/31 14:20
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptGroupReportPageRequest 对象", title = "实验小组报告查询请求")
public class ExptGroupReportPageRequest extends BasePageRequest {
    @Schema(title = "实验实例ID")
    @NotBlank(message = "实验实例ID 不能为空")
    private String exptInstanceId;
}
