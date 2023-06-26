package org.dows.hep.api.tenant.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.enums.ExperimentModeEnum;
import org.dows.hep.api.enums.ExperimentStateEnum;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentList 对象", title = "实验列表")
public class ExperimentListResponse{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "参与者类型[0:教师，1:组长，2：学生]")
    private Integer participatorType;

    @Schema(title = "参与者类型描述[0:教师，1:组长，2：学生]")
    private String participatorTypeDescr;

    @Schema(title = "参与者序号")
    private Integer participatorNo;

    @Schema(title = "参与者状态[0: 未准备 1:已准备 2:选择阶段中 3:已选择阶段]")
    private Integer participatorState;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "组序号")
    private String groupNo;

    @Schema(title = "案例名称[社区名]")
    private String caseName;

    @Schema(title = "实验名称")
    private String experimentName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "开始时间")
    private Date experimentStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "结束时间,实验结束时回填")
    private Date experimentEndTime;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;

    @Schema(title = "实验状态描述[默认未开始状态0~6步]")
    private String stateDescr;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "实验模式描述[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private String modelDescr;

    @Schema(title = "分配人名字")
    private String appointorName;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "参与者")
    private String participators;

    @Schema(title = "参与者基本信息")
    private List<ExperimentParticipatorResponse> participatorList;

    public String getModelDescr(){
        String str = "";
        if(model != null) {
            ExperimentModeEnum experimentModeEnum = Arrays
                    .stream(ExperimentModeEnum.values()).filter(e -> model == e.getCode())
                    .findFirst().orElse(null);
            if (experimentModeEnum != null) {
                str = experimentModeEnum.getDescr();
            } else {
                throw new RuntimeException("实验模式不存在");
            }
        }
        return str;
    }


    public String getStateDescr(){
        ExperimentStateEnum experimentModeEnum = Arrays
                .stream(ExperimentStateEnum.values()).filter(e -> state == e.getState())
                .findFirst().orElse(null);
        if(experimentModeEnum != null){
            return experimentModeEnum.getDescr();
        } else {
            throw new RuntimeException("实验状态不存在");
        }
    }

}
