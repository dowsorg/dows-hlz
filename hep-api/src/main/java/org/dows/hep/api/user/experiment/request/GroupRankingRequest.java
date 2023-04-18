package org.dows.hep.api.user.experiment.request;

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
@Schema(name = "GroupRanking 对象", title = "小组排行")
public class GroupRankingRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "期数")
    private String periods;


}
