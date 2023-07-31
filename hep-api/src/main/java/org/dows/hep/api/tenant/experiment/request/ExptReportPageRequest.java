package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.dows.hep.api.BasePageRequest;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告查询请求
 * @date 2023/7/31 10:40
 **/
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptReportPageRequest 对象", title = "实验报告查询请求")
public class ExptReportPageRequest extends BasePageRequest {
    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "根据实验状态过滤")
    private Integer filterByExptState;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByExptNameAsc;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByAllotTimeAsc;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByStartTimeAsc;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByEndTimeAsc;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByAllotUserNameAsc;

    @Schema(title = "0-降序 1-升序")
    private Integer sortByExptModeAsc;

}
