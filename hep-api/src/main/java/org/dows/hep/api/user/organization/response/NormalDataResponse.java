package org.dows.hep.api.user.organization.response;

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
@Schema(name = "NormalData 对象", title = "分类实例")
public class NormalDataResponse{
    @Schema(title = "分类名称")
    private String name;

    @Schema(title = "所占百分比")
    private BigDecimal per;

    @Schema(title = "总人数")
    private String sum;


}
