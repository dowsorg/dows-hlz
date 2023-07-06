package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 小组期数排行
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentGroupPeroidRankingResponse 对象", title = "小组每期排行榜")
public class ExperimentGroupPeroidRankingResponse {

    @Schema(title = "实验期数")
    private Integer peroid;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "小组别名")
    private String groupAlias;
    @Schema(title = "小组总分")
    private String totalScore;

    @Schema(title = "期数分数")
    private List<ScoreCateg> scoreCategs;




    @Data
    public class ScoreCateg{
        @Schema(title = "分数类目名[知识答题，医疗占比，健康指数]")
        private String scoreCateg;
        @Schema(title = "分数占比[知识答题，医疗占比，健康指数]百分比")
        private String proportion;
        @Schema(title = "分数类目code[知识答题:knowledge，医疗占比:，健康指数:]")
        private String scoreCode;
    }


}
