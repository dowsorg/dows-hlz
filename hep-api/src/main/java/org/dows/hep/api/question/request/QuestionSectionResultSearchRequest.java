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
@Schema(name = "QuestionSectionResultSearch 对象", title = "关键字聚合")
public class QuestionSectionResultSearchRequest{
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "appId")
    private String appId;


}
