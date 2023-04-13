package org.dows.hep.api.evaluate.response;

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
@Schema(name = "EvaluateQuestionnaire 对象", title = "评估问卷")
public class EvaluateQuestionnaireResponse{
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "分布式ID")
    private String evaluateQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集")
    private String questionSectionId;


}
