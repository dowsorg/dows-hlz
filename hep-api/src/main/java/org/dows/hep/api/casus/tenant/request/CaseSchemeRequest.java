package org.dows.hep.api.casus.tenant.request;

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
@Schema(name = "CaseScheme 对象", title = "案例方案")
public class CaseSchemeRequest{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "类别ID")
    private String categId;

    @Schema(title = "方案提示")
    private String tips;

    @Schema(title = "方案说明")
    private String schemeDescr;

    @Schema(title = "问题集")
    private String questionSectionRequest;

    @Schema(title = "来源[admin|tenant|user]")
    private String source;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "是否包含视频[0-否|1-是]")
    private Integer containsVideo;

    @Schema(title = "状态[0-关闭|1-开启]")
    private Integer enabled;


}
