package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/4/27 10:18
 */
@Data
@NoArgsConstructor
@Schema(name = "CasePersonIndicatorFunc 对象", title = "人物-功能点")
public class CasePersonIndicatorFuncRequest {

    @Schema(title = "案例人物功能点ID")
    private String casePersonIndicatorFuncId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "案例人物ID")
    private String casePersonId;

    @Schema(title = "背景图片")
    private String background;

}
