package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SaveSportItem 对象", title = "运动项目信息")
public class SaveSportItemRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "数据库id，新增时为空")
    private Long id;

    @Schema(title = "运动项目id，新增时为空")
    private String sportItemId;

    @Schema(title = "运动项目名称")
    private String sportItemName;


    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "运动强度(MET)")
    private String strengthMet;

    @Schema(title = "运动强度类别 运动强度类别 低|中|高")
    private String strengthType;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "关联指标json对象")
    private List<InterveneIndicatorVO> indicators;


}
