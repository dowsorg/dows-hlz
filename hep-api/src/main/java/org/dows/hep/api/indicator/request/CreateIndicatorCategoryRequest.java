package org.dows.hep.api.indicator.request;

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
@Schema(name = "CreateIndicatorCategory 对象", title = "创建指标目录对象")
public class CreateIndicatorCategoryRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "父ID")
    private String pid;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "分类code")
    private String categoryCode;


}
