package org.dows.hep.api.casus.tenant.response;

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
@Schema(name = "CaseQuestionnaire 对象", title = "案例问卷Response")
public class CaseQuestionnaireResponse{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "期数排序")
    private Integer periodSequence;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

    @Schema(title = "问题集ID")
    private String questionSectionId;


}
