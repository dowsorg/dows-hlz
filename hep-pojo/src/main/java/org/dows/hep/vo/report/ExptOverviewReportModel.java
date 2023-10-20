package org.dows.hep.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 总报告
 * @date 2023/7/19 14:04
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptOverviewReportModel 对象", title = "实验 `总报告` 填充数据")
public class ExptOverviewReportModel implements ExptReportModel {
    @Schema(title = "基本信息")
    private ExptBaseInfoModel baseInfo;

    @Schema(title = "实验信息")
    private ExptInfo exptInfo;

    @Schema(title = "总排行榜")
    private List<TotalRanking> totalRankingList;

    @Schema(title = "方案设计排行榜")
    private List<SchemeRanking> schemeRankingList;

    @Schema(title = "沙盘对抗排行榜")
    private List<SandGroupRanking> sandGroupRankingList;

    @Schema(title = "期数排行榜")
    private List<List<SandPeriodRanking>> sandPeriodRankingList;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExptInfo {
        @Schema(title = "实验名称")
        private String experimentName;

        @Schema(title = "实验日期")
        private String exptStartDate;

        @Schema(title = "期数")
        private Integer periods;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TotalRanking {
        private String groupNo;
        private String groupName;
        private String schemeScore;
        private String sandScore;
        private String totalScore;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchemeRanking {
        private String groupNo;
        private String groupName;
        private String schemeScore;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SandGroupRanking {
        private String groupNo;
        private String groupName;
        private List<PeriodGroupScore> periodGroupScoreList;
        private String groupScore;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PeriodGroupScore {
        private Integer period;
        private String score;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SandPeriodRanking {
        private String groupNo;
        private String groupName;
        private String healthIndexScore;
        private String knowledgeScore;
        private String treatmentPercentScore;

        private String operateRightScore;

        private String totalScore;

    }
}
