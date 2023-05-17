package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/15 17:01
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseCategoryResponse 对象", title = "案例类目Response")
public class CaseCategoryResponse {
    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "类别父id")
    private String caseCategPid;

    @Schema(title = "类别组")
    private String caseCategGroup;

    @Schema(title = "类别名")
    private String caseCategName;

    @Schema(title = "序列号")
    private Integer sequence;

    @Schema(title = "子")
    private List<CaseCategoryResponse> children;
}
