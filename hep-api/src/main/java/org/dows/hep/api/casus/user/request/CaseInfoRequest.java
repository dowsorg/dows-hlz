package org.dows.hep.api.casus.user.request;

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
@Schema(name = "CaseInfo 对象", title = "提示信息")
public class CaseInfoRequest{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "[descr:背景|guide:帮助中心|scoresPrompt:评分提示]")
    private String type;


}
