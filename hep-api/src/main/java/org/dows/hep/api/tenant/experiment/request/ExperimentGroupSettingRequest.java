package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author
 * @description
 * @date
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "GroupSetting 对象", title = "小组设置")
public class ExperimentGroupSettingRequest {
    @Schema(title = "应用ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String appId;

    @Schema(title = "案列ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private  String caseInstanceId;
    @Schema(title = "实验实列ID",requiredMode = Schema.RequiredMode.REQUIRED)
    private String experimentInstanceId;
    @Schema(title = "实验名称",requiredMode = Schema.RequiredMode.REQUIRED)
    private String experimentName;
    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]",requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer model;
    @Schema(title = "实验开始时间",requiredMode = Schema.RequiredMode.REQUIRED)
    private Date startTime;

    @Schema(title = "实验小组设置")
    public List<GroupSetting> groupSettings;

    @Data
    public static class GroupSetting {

        @Schema(title = "小组序号")
        private String groupNo;
//
//        @Schema(title = "小组名称")
//        private String groupName;

        @Schema(title = "小组别名[第1组，第2组...]")
        private String groupAlias;

        @Schema(title = "成员数量")
        private Integer memberCount;

        @Schema(title = "参与者对象")
        private List<ExperimentParticipator> experimentParticipators;

    }


    @Data
    public static class ExperimentParticipator {
        @Schema(title = "主键ID")
        private String id;
        @Schema(title = "参与者顺序")
        private Integer seq;
        @Schema(title = "参与者ID",requiredMode = Schema.RequiredMode.REQUIRED)
        private String participatorId;
        @Schema(title = "参与者名称",requiredMode = Schema.RequiredMode.REQUIRED)
        private String participatorName;

    }

}

