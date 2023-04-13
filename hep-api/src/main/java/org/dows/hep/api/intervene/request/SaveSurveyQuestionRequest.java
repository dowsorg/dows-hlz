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
@Schema(name = "SaveSurveyQuestion 对象", title = "保存问卷题目信息")
public class SaveSurveyQuestionRequest{
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "问题列表json")
    private String questions;


}
