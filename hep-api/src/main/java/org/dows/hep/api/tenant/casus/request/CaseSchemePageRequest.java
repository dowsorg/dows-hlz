package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.BasePageRequest;

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
public class CaseSchemePageRequest extends BasePageRequest {
    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "类别ID")
    private List<String> categIds;

    @Schema(title = "启用状态")
    private Integer enabled;

}
