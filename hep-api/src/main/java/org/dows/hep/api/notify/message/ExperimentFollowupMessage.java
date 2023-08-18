package org.dows.hep.api.notify.message;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * 实验随访消息
 */
@Data
@Builder
public class ExperimentFollowupMessage implements ExperimentMessage {
    // 是否可以随访[true，false]
    private Boolean flag;
    @Schema(title = "实验id")
    private String experimentId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验期数")
    private Integer periods;

    @Schema(title = "操作者id")
    private String operatorId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "功能点id")
    private String indicatorFuncId;

    @Schema(title = "机构id")
    private String experimentOrgId;

    @Schema(title = "监测随访id")
    private String indicatorViewMonitorFollowupId;

    @Schema(title = "间隔天数")
    private Integer intervalDay;

}
