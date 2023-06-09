package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Schema(name = "ExperimentSetting 对象", title = "实验设置")
public class ExperimentSetting {

    @Schema(title = "配置key[标准模式，沙盘模式，方案设计...]")
    private SchemeSetting schemeSetting;

    @Schema(title = "key对应的json配置")
    private SandSetting sandSetting;


    /**
     * 方案设计设置
     */
    @Data
    public static class SchemeSetting {
        // 方案设计时长
        @Schema(title = "方案设计时长",requiredMode = Schema.RequiredMode.REQUIRED)
        private Long duration;
        // 方案设计权重
        @Schema(title = "方案设计权重",requiredMode = Schema.RequiredMode.REQUIRED)
        private Float weight;
        // 设计截止时间
        @Schema(title = "设计截止时间",requiredMode = Schema.RequiredMode.REQUIRED)
        private Date schemeEndTime;
        // 评分截止时间
        @Schema(title = "评分截止时间",requiredMode = Schema.RequiredMode.REQUIRED)
        private Date scoreEndTime;

    }


    /**
     * 沙盘设置
     */
    @Data
    public static class SandSetting{
        // 期数
        @Schema(title = "期数",requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer periods;
        // 每期间隔
        @Schema(title = "每期间隔",requiredMode = Schema.RequiredMode.REQUIRED)
        private Long interval;
        // 每期时长
        @Schema(title = "每期时长",requiredMode = Schema.RequiredMode.REQUIRED)
        private Map<String,Integer> durationMap;
        // 每期权重
        @Schema(title = "每期权重",requiredMode = Schema.RequiredMode.REQUIRED)
        private Map<String,Float> weightMap;
        // 模拟时间周期
        @Schema(title = "模拟时间周期",requiredMode = Schema.RequiredMode.REQUIRED)
        private Map<String,Integer> periodMap;

        // 健康指数权重
        @Schema(title = "健康指数权重",requiredMode = Schema.RequiredMode.REQUIRED)
        private Float healthIndexWeight;
        // 知识考点权重
        @Schema(title = "知识考点权重",requiredMode = Schema.RequiredMode.REQUIRED)
        private Float knowledgeWeight;
        // 医疗占比权重
        @Schema(title = "医疗占比权重",requiredMode = Schema.RequiredMode.REQUIRED)
        private Float medicalRatioWeight;
    }

}
