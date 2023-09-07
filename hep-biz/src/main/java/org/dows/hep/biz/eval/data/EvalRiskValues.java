package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:18
 */
@Data
@Accessors(chain = true)
public class EvalRiskValues {
    @Schema(title = "人群id")
    private String crowdId;

    @Schema(title = "危险因素id")
    private String riskId;

    @Schema(title = "危险因素名称")
    private String riskName;
}
