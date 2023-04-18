package org.dows.hep.api.user.experiment.response;

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
@Schema(name = "CountDown 对象", title = "时间")
public class CountDownResponse{
    @Schema(title = "沙盘时间")
    private Long sandTime;

    @Schema(title = "沙盘时间单位")
    private String sandTimeUnit;

    @Schema(title = "方案时间")
    private Long schemeTime;

    @Schema(title = "方案时间单位")
    private String schemeTimeUnit;


}
