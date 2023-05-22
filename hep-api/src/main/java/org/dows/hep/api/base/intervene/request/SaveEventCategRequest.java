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
@Schema(name = "SaveEventCateg 对象", title = "类别信息")
public class SaveEventCategRequest {

    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "数据库id，新增时为空")
    private Long id;

    @Schema(title = "类别分布式id，新增时为空")
    private String categId;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "父类别分布式id")
    private String categPid;


}
