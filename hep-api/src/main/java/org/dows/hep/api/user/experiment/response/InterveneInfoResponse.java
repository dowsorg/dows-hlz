package org.dows.hep.api.user.experiment.response;

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
@Schema(name = "InterveneInfo 对象", title = "干预记录")
public class InterveneInfoResponse{
    @Schema(title = "干预记录id")
    private String operateInterveneId;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作[干预]类型1-饮食 2-运动 3-心理 4-治疗 ")
    private String operateType;

    @Schema(title = "状态完整快照json")
    private String operateContextJson;


}
