package org.dows.hep.api.user.materials.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author fhb
 * @description
 * @date 2023/4/18 15:02
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsSearchRequest 对象", title = "关键字聚合")
public class MaterialsSearchRequest{

    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字-标题/作者")
    private String keyword;

}
