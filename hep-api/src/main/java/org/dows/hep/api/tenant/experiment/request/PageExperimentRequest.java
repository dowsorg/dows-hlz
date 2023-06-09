package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "实验分页请求")
public class PageExperimentRequest {
    @Schema(title = "页号",requiredMode= Schema.RequiredMode.REQUIRED)
    private Integer pageNo = 1;
    @Schema(title = "页大小",requiredMode= Schema.RequiredMode.REQUIRED)
    private Integer pageSize = 10;

    private String keyword;

    private String orderBy;
    // 默认降序
    private Boolean desc = true;
}
