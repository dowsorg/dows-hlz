package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "OrgPerson 对象", title = "人物列表")
public class OrgPersonResponse{
    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "挂号状态，0-未挂号 1-已挂号")
    private Integer flowState;

    @Schema(title = "剩余资金")
    private BigDecimal asset;


}
