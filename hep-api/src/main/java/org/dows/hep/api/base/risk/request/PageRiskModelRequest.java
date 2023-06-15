package org.dows.hep.api.base.risk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.crud.api.model.PageRequest;

/**
 * @author jx
 * @date 2023/6/15 19:48
 */
@Data
@Schema(title = "风险模型 分页请求")
public class PageRiskModelRequest extends PageRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "搜索关键字", description = "搜索关键字")
    private String keyword;
}
