package org.dows.hep.api.casus.tenant.response;

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
@Schema(name = "CaseInstance 对象", title = "案例Response")
public class CaseInstanceResponse{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例名称")
    private String caseName;

    @Schema(title = "案例图片")
    private String casePic;

    @Schema(title = "案例类型")
    private String caseType;

    @Schema(title = "背景描述")
    private String descr;

    @Schema(title = "指导描述")
    private String guide;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "案例状态[0:未发布|1:发布]")
    private Integer state;


}
