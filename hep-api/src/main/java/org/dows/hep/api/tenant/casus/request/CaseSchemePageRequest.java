package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CaseSchemePage 对象", title = "关键字聚合")
public class CaseSchemePageRequest{
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "类别ID")
    private List<String> categIds;

    @Schema(title = "启用状态")
    private Integer enabled;

}
