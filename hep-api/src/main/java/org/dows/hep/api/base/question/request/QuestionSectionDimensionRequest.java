package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private BigDecimal score;


    // JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    @JsonIgnore
    private Long id;

    @Schema(title = "来源")
    @JsonIgnore
    private String source;

    @Schema(title = "创建者账号Id")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者姓名")
    @JsonIgnore
    private String accountName;
}
