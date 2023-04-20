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
@Schema(name = "ScoreRank 对象", title = "排名列表")
public class ScoreRankResponse{
    @Schema(title = "期数")
    private String periods;

    @Schema(title = "排名")
    private Integer ranking;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组名称")
    private String groupName;

    @Schema(title = "健康指数得分")
    private String healthIndex;

    @Schema(title = "知识考点得分")
    private String knowledgeScore;

    @Schema(title = "医疗占比得分")
    private String medicalProportion;

    @Schema(title = "总分")
    private String totalSocre;


}
