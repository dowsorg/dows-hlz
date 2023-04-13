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
@Schema(name = "CreateOrUpdateIndicatorCategory 对象", title = "创建活更新指标目录列表")
public class CreateOrUpdateIndicatorCategoryRequest{
    @Schema(title = "分布式ID")
    private String indicatorCategoryId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "父ID")
    private Long pid;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "分类code")
    private String categoryCode;


}
