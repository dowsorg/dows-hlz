package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.enums.EnumParticipatorType;

import java.util.Arrays;

@Data
@NoArgsConstructor
@Schema(name = "GetExperimentGroupCaptainResponse 对象", title = "实验小组组长信息")
public class GetExperimentGroupCaptainResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验参与者ID")
    private String experimentParticipatorId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验名称")
    private String experimentName;

    @Schema(title = "实验机构ID")
    private String experimentOrgIds;

    @Schema(title = "实验机构名称")
    private String experimentOrgNames;

    @Schema(title = "实验方案设计ItemId")
    private String experimentSchemeItemIds;

    @Schema(title = "组员账号ID")
    private String accountId;

    @Schema(title = "组员账号名")
    private String accountName;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "组序号")
    private String groupNo;

    @Schema(title = "参与者序号")
    private Integer participatorNo;

    @Schema(title = "参与者类型[0:教师，1:组长，2：学生]")
    private Integer participatorType;
    @Schema(title = "参与者类型描述[0:教师，1:组长，2：学生]")
    private String participatorTypeDescr;

    @Schema(title = "参与者状态[0: 未准备 1:已准备 2:选择阶段中 3:已选择阶段]")
    private Integer participatorState;

    @Schema(title = "实验者状态[默认未开始状态0~6步]")
    private Boolean state;


    public String getParticipatorTypeDescr() {
        EnumParticipatorType enumParticipatorType = Arrays.stream(EnumParticipatorType.values())
                .filter(pt -> pt.getCode() == participatorType).findFirst().orElse(null);

        if (enumParticipatorType != null) {
            return enumParticipatorType.getDescr();
        }
        return null;
    }


}
