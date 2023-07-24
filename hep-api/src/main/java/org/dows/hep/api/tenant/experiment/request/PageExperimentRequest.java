package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.crud.api.model.PageRequest;

@Data
@Schema(title = "实验分页请求")
public class PageExperimentRequest extends PageRequest {

    @Schema(title = "搜索关键字", description = "搜索关键字")
    private String keyword;

    @Schema(title = "账号ID", description = "学生账号ID")
    private String accountId;

    @Schema(title = "实验实例ID", description = "实验实例ID")
    private String experimentInstanceId;
}
