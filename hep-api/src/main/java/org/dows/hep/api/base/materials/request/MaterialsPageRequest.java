package org.dows.hep.api.base.materials.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/27 10:16
 */
@Data
@NoArgsConstructor
@Schema(name = "MaterialsPageRequest 对象", title = "资料")
public class MaterialsPageRequest {

    @Schema(title = "pageNo")
    private Long pageNo;

    @Schema(title = "pageSize")
    private Long pageSize;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "bizCode")
    private String bizCode;

    @Schema(title = "关键字")
    private String keyword;
}
