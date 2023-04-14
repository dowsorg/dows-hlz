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
@Schema(name = "UpdateIndicatorCategory 对象", title = "更新指标目录")
public class UpdateIndicatorCategoryRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "创建活更新指标目录列表")
    private String ListCreateOrUpdateIndicatorCategory;


}
