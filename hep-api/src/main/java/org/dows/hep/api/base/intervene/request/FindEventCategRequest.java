package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "FindEventCateg 对象", title = "查询条件")
public class FindEventCategRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "父类别")
    private String pid;

    @Schema(title = "是否包含子节点 0-否 1-是")
    private Integer withChild;


}
