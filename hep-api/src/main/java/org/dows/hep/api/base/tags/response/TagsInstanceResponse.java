package org.dows.hep.api.base.tags.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;

import java.util.List;

/**
 * @author jx
 * @date 2023/6/14 17:10
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "TagsInstanceResponse 对象", title = "标签实例response")
public class TagsInstanceResponse {

    @Schema(title = "标签公式ID")
    private String tagsFormulaId;

    @Schema(title = "标签分布式ID")
    private String tagsId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "标签名称")
    private String name;

    @Schema(title = "标签分类ID")
    private String tagsCategoryId;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @Schema(title = "指标公式")
    private List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList;
}
