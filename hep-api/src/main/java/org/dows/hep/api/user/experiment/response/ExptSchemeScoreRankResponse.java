package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计排行榜
 * @date 2023/7/19 11:50
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptSchemeScoreRankResponse 对象", title = "实验方案设计排行榜")
public class ExptSchemeScoreRankResponse {
    @Schema(title = "组ID")
    private String groupId;

    @Schema(title = "组数")
    private String groupNo;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "得分")
    private String score;
}
