package org.dows.hep.api.experiment.user.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "ExperimentGroup 对象", title = "实验小组信息")
public class ExperimentGroupResponse{
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Integer state;


}
