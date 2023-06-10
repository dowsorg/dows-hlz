package org.dows.hep.api.tenant.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.enums.ExperimentModeEnum;
import org.dows.hep.api.enums.ExperimentStateEnum;

import java.util.Arrays;
import java.util.Date;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExperimentList 对象", title = "实验列表")
public class ExperimentListResponse{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "案例名称[社区名]")
    private String caseName;

    @Schema(title = "实验名称")
    private String experimentName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "开始时间")
    private Date startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "结束时间,实验结束时回填")
    private Date endTime;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;

    @Schema(title = "实验状态描述[默认未开始状态0~6步]")
    private String stateDescr;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "实验模式描述[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private String modelDescr;

    public String getModelDescr(){
        ExperimentModeEnum experimentModeEnum = Arrays
                .stream(ExperimentModeEnum.values()).filter(e -> model == e.getState())
                .findFirst().orElse(null);
        if(experimentModeEnum != null){
            return experimentModeEnum.getDescr();
        } else {
            throw new RuntimeException("实验模式不存在");
        }
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
