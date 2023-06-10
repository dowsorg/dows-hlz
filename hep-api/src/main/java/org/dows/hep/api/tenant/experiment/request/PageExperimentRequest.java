package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Schema(title = "实验分页请求")
public class PageExperimentRequest {
    @NotNull(message = "页数为必填字段")
    @Schema(title = "页号", description="页号",requiredMode= Schema.RequiredMode.REQUIRED)
    private int pageNo = 1;
    @NotNull(message = "页大小必填字段")
    @Schema(title = "页大小", description="页大小",requiredMode= Schema.RequiredMode.REQUIRED)
    private int pageSize = 10;

    @Schema(title = "搜索关键字", description="搜索关键字")
    private String keyword;

    @Schema(title = "账号ID", description="学生账号ID")
    private String accountId;

    private String orderBy;
    // 默认降序
    private boolean desc = true;
}
