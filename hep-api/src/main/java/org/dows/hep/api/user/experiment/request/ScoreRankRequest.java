package org.dows.hep.api.user.experiment.request;

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
@Schema(name = "ScoreRank 对象", title = "分数排行")
public class ScoreRankRequest {
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "期数")
    private String periods;


}
