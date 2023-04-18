package org.dows.hep.api.user.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "AllocationScheme 对象", title = "分配方案Response")
public class AllocationSchemeResponse{
    @Schema(title = "案例响应")
    private  caseSchemeResponse;


}
