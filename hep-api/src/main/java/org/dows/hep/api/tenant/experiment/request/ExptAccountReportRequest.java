package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dows.hep.api.BasePageRequest;

/**
 * @author fhb
 * @version 1.0
 * @description 个人查询实验报告请求
 * @date 2023/7/31 15:37
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptAccountReportRequest 对象", title = "个人查询实验报告请求")
public class ExptAccountReportRequest extends BasePageRequest {
    @Schema(title = "学生账号ID")
    private String accountId;
}
