package org.dows.hep.api.user.materials.request;

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
@Schema(name = "QuestionSearch 对象", title = "关键字聚合")
public class QuestionSearchRequest{
    @Schema(title = "应用ID")
    private String appId;


}
