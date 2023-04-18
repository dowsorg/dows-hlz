package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CreateExperiment 对象", title = "创建实验对象")
public class CreateExperimentRequest{
    @Schema(title = "案例ID")
    private String caseId;

    @Schema(title = "实验名称")
    private String experimentName;

    @Schema(title = "实验说明")
    private String experimentDescr;

    @Schema(title = "开始时间")
    private java.time.LocalDateTime startTime;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "实验设置JSON对象")
    private String experimentSetting;


}
