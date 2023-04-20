package org.dows.hep.api.user.organization.response;

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

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "指标值")
    private String indicatorValue;

    @Schema(title = "标签")
    private String tags;

    @Schema(title = "创建者")
    private String creator;


}
