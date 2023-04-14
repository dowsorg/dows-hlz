package org.dows.hep.api.base.indicator.response;

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
@Schema(name = "IndicatorRef 对象", title = "指标引用列表")
public class IndicatorRefResponse {
    @Schema(title = "引用这个指标的指标ID")
    private String refIndicatorId;


}
