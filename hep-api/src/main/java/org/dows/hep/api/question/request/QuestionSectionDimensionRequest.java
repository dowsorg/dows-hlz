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
@Schema(name = "QuestionSectionDimension 对象", title = "维度Request")
public class QuestionSectionDimensionRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "维度ID")
    private String questionSectionDimensionId;

    @Schema(title = "维度名称")
    private String demensionName;

    @Schema(title = "内容")
    private String demensionContent;

    @Schema(title = "分数")
    private java.math.BigDecimal score;


}
