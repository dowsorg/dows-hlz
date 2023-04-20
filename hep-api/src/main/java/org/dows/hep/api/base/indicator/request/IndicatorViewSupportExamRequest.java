package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "IndicatorViewSupportExam 对象", title = "查看指标辅助检查类")
public class IndicatorViewSupportExamRequest{
    @Schema(title = "查看指标辅助检查类分布式Id")
    private String indicatorViewSupportExamId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;


}
