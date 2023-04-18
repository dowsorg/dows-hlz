package org.dows.hep.api.user.organization.request;

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
@Schema(name = "AgeRatio 对象", title = "年龄分类")
public class AgeRatioRequest{
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;


}
