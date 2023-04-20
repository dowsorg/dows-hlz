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
@Schema(name = "SaveInterveneCateg 对象", title = "类别信息")
public class SaveInterveneCategRequest{
    @Schema(title = "分布式id")
    private String eventCategId;

    @Schema(title = "名称")
    private String categName;

    @Schema(title = "分布式父id")
    private String categPid;

    @Schema(title = "类别key")
    private String section;


}
