package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "GroupRanking 对象", title = "小组排行榜")
public class GroupRankingResponse {
    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组名称")
    private String groupName;

    @Schema(title = "总分")
    private String totalSocre;

    @Schema(title = "健康指数占比")
    private String healthIndexPercent;

    @Schema(title = "知识考点占比")
    private String knowledgeScorePercent;

    @Schema(title = "医疗占比")
    private String medicalProportionPercent;


}
