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
@Schema(name = "QuestionSection 对象", title = "问题集Request")
public class QuestionSectionRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "类别Id")
    private String questionSectionCategId;

    @Schema(title = "问题集名称")
    private String name;

    @Schema(title = "问题集提示")
    private String tips;

    @Schema(title = "问题集说明")
    private String descr;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "问题集合")
    private String QuestionRequest;

    @Schema(title = "维度集合")
    private String QuestionSectionDimensionRequest;


}
