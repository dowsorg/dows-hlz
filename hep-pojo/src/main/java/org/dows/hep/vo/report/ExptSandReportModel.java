package org.dows.hep.vo.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionResponse;

import java.util.List;
import java.util.Map;

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
    // 基本信息
    private ExptBaseInfoModel baseInfo;
    // 总得分
    private Score totalScore;
    // 每期得分()
    private List<PeriodScore> periodScore;
    // 每期权重(计算)
    private Map<String, String> periodWeight;
    // 评分权重(读取实验setting)
    private Map<String, String> scoreWeight;

    //基本信息
    private List<NpcData> npcDatas;


    @Data
    public static class NpcData {
        //案例名称 npc 名称
        private String userName;
        private String sex;
        private String age;
        //疾病类别
        private String diseaseCateg;
        // 干预前风险指标
        private List<RiskIndicator> interveneBefores;
        //干预后风险指标
        private List<RiskIndicator> interveneAfters;
        //服务记录
        private List<ServiceLog> serviceLogs;
        // 知识考点
        private List<KnowledgeAnswer> peroidQuestions;


    }

    @Data
    public static class KnowledgeAnswer {

        private String categName;
        private List<QuestionResponse> questions;
        // 题目
        private String questionContent;
        // 用户的答案
        private String userAnswer;
        //参考答案
        private String rightAnswer;
        //解析
        private String analysis;


    }

    @Data
    public static class ServiceLog {
        //时间
        private String dt;
        //服务记录
        private String descr;
        //标签
        private String lable;

    }

    @Data
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

    }

    /**
     * 期数权重
     */
    @Data
    public static class PeriodWeight {
        // 期数
        private Integer peroid;

        private Score score;

    }

    /**
     * 期数分数
     */
    @Data
    public static class PeriodScore {
        // 期数
        private Integer peroid;

        private Score score;

    }


    /**
     * 分数
     */
    @Data
    public static class Score {
        private String healthIndex;
        private String knowledgeScore;
        private String treatmentPercent;
        private String totalScore;
        private String totalRanking;
    }


}
