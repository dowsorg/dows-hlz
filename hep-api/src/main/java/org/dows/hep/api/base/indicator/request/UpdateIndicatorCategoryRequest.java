package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "UpdateIndicatorCategory 对象", title = "更新指标目录")
public class UpdateIndicatorCategoryRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "创建活更新指标目录列表")
    private String ListCreateOrUpdateIndicatorCategory;


}
