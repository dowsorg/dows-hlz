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
@Schema(name = "CreateIndicatorCategory 对象", title = "创建指标目录对象")
public class CreateIndicatorCategoryRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "父ID")
    private String pid;

    @Schema(title = "分类名称")
    private String categoryName;

    @Schema(title = "分类code")
    private String categoryCode;


}
