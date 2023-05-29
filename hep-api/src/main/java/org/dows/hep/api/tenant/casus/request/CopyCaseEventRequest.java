package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/16 16:37
 */
@Data
@NoArgsConstructor
@Schema(name = "CopyCaseEvent 对象", title = "批量事件")

public class CopyCaseEventRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;


    @Schema(title = "人物accountId 数据库/案例人物")
    @NotEmpty(message = "人物ID不可为空")
    private String personId;

    @Schema(title = "人物名称 数据库/案例人物")
    private String personName;
    @Schema(title = "数据库事件id列表")
    @NotEmpty(message = "请选择要添加的数据库事件")
    private List<String> ids;
}
