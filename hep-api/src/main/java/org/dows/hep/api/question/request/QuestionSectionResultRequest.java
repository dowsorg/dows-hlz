package org.dows.hep.api.question.request;

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
@Schema(name = "QuestionSectionResult 对象", title = "答案Request")
public class QuestionSectionResultRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答案记录Item")
    private String QuestionSectionResultItem;


}
