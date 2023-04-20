package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "GroupSetting 对象", title = "小组设置")
public class GroupSettingRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组名称")
    private String groupName;

    @Schema(title = "小组别名[第1组，第2组...]")
    private String groupAlias;

    @Schema(title = "成员数量")
    private Integer memberCount;

    @Schema(title = "参与者对象")
    private List<ExperimentParticipator> experimentParticipators;



    @Data
    public static class ExperimentParticipator{
        @Schema(title = "参与者顺序")
        private Integer seq;
        @Schema(title = "参与者ID")
        private String participatorId;
        @Schema(title = "参与者名称")
        private String participatorName;

    }

}
