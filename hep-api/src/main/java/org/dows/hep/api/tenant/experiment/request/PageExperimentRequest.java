package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.crud.api.model.PageRequest;

@Data
@Schema(title = "实验分页请求")
public class PageExperimentRequest extends PageRequest {
/*    @NotNull(message = "页数为必填字段")
    @Schema(title = "页号", description="页号",requiredMode= Schema.RequiredMode.REQUIRED)
    private Integer pageNo = 1;
    @NotNull(message = "页大小必填字段")
    @Schema(title = "页大小", description="页大小",requiredMode= Schema.RequiredMode.REQUIRED)
    private Integer pageSize = 10;
    private String orderBy;
    // 默认降序
    private Boolean desc = true;*/


    @Schema(title = "搜索关键字", description = "搜索关键字")
    private String keyword;

    @Schema(title = "账号ID", description = "学生账号ID")
    private String accountId;

    @Schema(title = "实验实例ID", description = "实验实例ID")
    private String experimentInstanceId;
}
