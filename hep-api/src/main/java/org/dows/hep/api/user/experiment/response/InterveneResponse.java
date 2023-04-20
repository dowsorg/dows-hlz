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
@Schema(name = "Intervene 对象", title = "干预记录")
public class InterveneResponse{
    @Schema(title = "干预记录id")
    private String operateInterveneId;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作时间")
    private String operateTime;

    @Schema(title = "操作描述")
    private String descr;

    @Schema(title = "标签")
    private String tag;


}
