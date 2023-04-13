package org.dows.hep.api.intervene.request;

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
@Schema(name = "SetSurveyState 对象", title = "启用、禁用问卷")
public class SetSurveyStateRequest{
    @Schema(title = "问卷id")
    private String surveyId;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;


}
