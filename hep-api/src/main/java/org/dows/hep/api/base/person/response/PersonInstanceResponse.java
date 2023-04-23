package org.dows.hep.api.base.person.response;

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
@Schema(name = "PersonInstance 对象", title = "人物实例")
public class PersonInstanceResponse{
    @Schema(title = "姓名")
    private String name;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "用户简介")
    private String intro;


}
