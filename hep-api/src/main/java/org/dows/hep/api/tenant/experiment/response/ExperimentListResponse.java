package org.dows.hep.api.tenant.experiment.response;

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
@Schema(name = "ExperimentList 对象", title = "实验列表")
public class ExperimentListResponse{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "案例名称[社区名]")
    private String caseName;

    @Schema(title = "实验名称")
    private String experimentName;

    @Schema(title = "开始时间")
    private java.time.LocalDateTime startTime;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;


}
