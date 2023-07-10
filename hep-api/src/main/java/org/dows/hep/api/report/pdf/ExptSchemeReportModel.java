package org.dows.hep.api.report.pdf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计报告
 * @date 2023/7/7 11:00
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptBaseReportVO 对象", title = "实验报告基本信息")
public class ExptSchemeReportModel implements IExptReportModel {

    @Schema(title = "基本信息")
    private ExptBaseInfo baseInfo;

    @Schema(title = "小组信息")
    private GroupInfo groupInfo;

    @Schema(title = "得分信息")
    private ScoreInfo scoreInfo;

    @Schema(title = "方案设计信息")
    private SchemeInfo schemeInfo;

    @Schema(title = "答题信息")
    private List<QuestionInfo> questionInfos;

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
        private Date exptStartDate;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ScoreInfo {
        @Schema(title = "排名")
        private Integer ranking;

        @Schema(title = "小组得分")
        private Float score;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SchemeInfo {
        @Schema(title = "方案设计名称")
        private String schemeName;

        @Schema(title = "方案设计提示")
        private String schemeTips;

        @Schema(title = "方案设计内容")
        private String schemeDescr;
    }

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QuestionInfo {
        @Schema(title = "问题标题")
        private String questionTitle;

        @Schema(title = "问题描述")
        private String questionDescr;

        @Schema(title = "回答内容")
        private String questionResult;

        @Schema(title = "子问题")
        private List<QuestionInfo> children;
    }
}

