package org.dows.hep.api.base.question.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "QuestionSectionDimensionResponse 对象", title = "维度集Response")
public class QuestionSectionDimensionResponse{
    @Schema(title = "应用ID")
    private String appId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "维度ID")
    private String questionSectionDimensionId;

    @Schema(title = "维度名称")
    private String demensionName;

    @Schema(title = "内容")
    private String demensionContent;

    @Schema(title = "分数")
    private BigDecimal score;
}
