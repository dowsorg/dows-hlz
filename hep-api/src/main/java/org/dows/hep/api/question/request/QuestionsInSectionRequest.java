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
@Schema(name = "QuestionsInSection 对象", title = "问卷中条件查询")
public class QuestionsInSectionRequest{
    @Schema(title = "appId")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "类别Id")
    private String categId;

    @Schema(title = "题目答题类型[单选|多选|判断|主观|材料]")
    private String questionType;

    @Schema(title = "题目")
    private String questionName;


}
