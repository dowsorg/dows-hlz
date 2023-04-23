package org.dows.hep.api.base.person.request;

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
@Schema(name = "PersonInstance 对象", title = "人物-实例")
public class PersonInstanceRequest{
    @Schema(title = "账户ID")
    private Long accountId;

    @Schema(title = "姓名")
    private String name;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "用户简介")
    private String intro;


}
