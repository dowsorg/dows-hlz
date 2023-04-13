package org.dows.hep.api.person.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

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
    private Long String*accountId;

    @Schema(title = "姓名")
    private String name;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "用户简介")
    private String intro;


}
