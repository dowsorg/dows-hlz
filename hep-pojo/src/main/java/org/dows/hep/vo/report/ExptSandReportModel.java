package org.dows.hep.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @description 实验 `沙盘pdf报告` 填充数据
 * @date 2023/7/7 11:00
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptBaseReportVO 对象", title = "实验 `沙盘pdf报告` 填充数据")
public class ExptSandReportModel implements ExptReportModel {
    @Schema(title = "基本信息")
    private ExptBaseInfoModel baseInfo;

    @Schema(title = "小组信息")
    private GroupInfo groupInfo;

    @Schema(title = "得分信息")
    private ScoreInfo scoreInfo;

    @Schema(title = "基本信息")
    private List<NpcData> npcDatas;

    @Schema(title = "期数问题")
    private List<List<KnowledgeAnswer>> periodQuestions;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupInfo {
        @Schema(title = "组数")
        private String groupNo;

        @Schema(title = "组名")
        private String groupName;

        @Schema(title = "组员-列表形式")
        private List<String> groupMembers;

        @Schema(title = "实验社区")
        private String caseName;

        @Schema(title = "实验名称")
        private String experimentName;

        @Schema(title = "实验日期")
        private String exptStartDate;

        @Schema(title = "案例数量")
        private int caseNum;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreInfo {
        private Score totalScore;
        private List<PeriodScore> periodScores;
        private List<PeriodWeight> periodWeights;
        private ScoreWeight scoreWeight;

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Score {
            private String healthIndexScore;
            private String knowledgeScore;
            private String treatmentPercentScore;
            private String totalScore;
            private String totalRanking;
        }

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PeriodScore {
            private String periods;
            private Score scoreInfo;
        }

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class PeriodWeight {
            private String periods;
            private String weight;
        }

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class ScoreWeight {
            private String healthIndexWeight;
            private String knowledgeWeight;
            private String treatmentPercentWeight;
        }
    }

    @Data
    public static class NpcData {
        // 基本信息
        //private NpcBaseInfo baseInfo;
        // 干预前风险指标
        private PersonRiskFactor interveneBefores;
        //干预后风险指标
        private PersonRiskFactor interveneAfters;
        //服务记录
        private List<ServiceLog> serviceLogs = new ArrayList<>();
    }

/*    @Data
    public static class NpcBaseInfo {
        private Integer no;
        private String userName;
        private String sex;
        private String age;
        //疾病类别
        private String diseaseCateg;
    }*/

    /*@Data
    public static class RiskIndicator {
        //死亡原因
        private String deathReason;
        //死亡概率（1/10万）
        private String deathProbability;
        //健康危险因素
        private String riskFactor;
        //指标值
        private String indicator;
        //危险分数
        private String dangerScore;

    }*/

    @Data
    public static class ServiceLog {
        //时间
        private String dt;
        //服务记录
        private String descr;
        //标签
        private String lable;

    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class KnowledgeAnswer {
        private String categName;
        private List<QuestionInfo> questionInfos;

        @Builder
        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        public static class QuestionInfo {
            // 题目
            private String questionTitle;
            // 题目选项
            private List<String> questionOptions;
            // 用户的答案
            private String userAnswer;
            //参考答案
            private String rightAnswer;
            //解析
            private String analysis;
            // 子
            private List<QuestionInfo> children;
        }
    }



}
