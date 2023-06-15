package org.dows.hep.api.base.risk.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.crud.api.model.PageRequest;

/**
 * @author jx
 * @date 2023/6/15 15:11
 */
@Data
@Schema(title = "人群类别 分页请求")
public class PageCrowdsRequest extends PageRequest {
    @Schema(title = "搜索关键字", description = "搜索关键字")
    private String keyword;
}
