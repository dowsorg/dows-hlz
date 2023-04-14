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
@Schema(name = "QuestionnaireMergeElements 对象", title = "试卷合并因素")
public class QuestionnaireMergeElementsRequest{
    @Schema(title = "问题集[试卷]ids")
    private String questionSectionIds;

    @Schema(title = "appId")
    private String appId;


}
