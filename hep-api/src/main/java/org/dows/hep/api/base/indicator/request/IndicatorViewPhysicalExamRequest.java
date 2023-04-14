package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "IndicatorViewPhysicalExam 对象", title = "查看指标体格检查类")
public class IndicatorViewPhysicalExamRequest {
    @Schema(title = "查看指标体格检查类分布式Id")
    private String IndicatorViewPhysicalExamId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
