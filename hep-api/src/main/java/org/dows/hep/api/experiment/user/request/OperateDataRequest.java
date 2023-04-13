package org.dows.hep.api.experiment.user.request;

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
@Schema(name = "OperateData 对象", title = "操作数据")
public class OperateDataRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "操作账号ID")
    private String accountId;

    @Schema(title = "json数据")
    private String dataJson;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "操作类型[饮食|运动|心理...]")
    private String operateType;


}
