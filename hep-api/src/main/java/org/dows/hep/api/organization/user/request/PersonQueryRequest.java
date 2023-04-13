package org.dows.hep.api.organization.user.request;

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
@Schema(name = "PersonQuery 对象", title = "关键字")
public class PersonQueryRequest{
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "用户名")
    private String userName;

    @Schema(title = "标签")
    private String tagName;


}
