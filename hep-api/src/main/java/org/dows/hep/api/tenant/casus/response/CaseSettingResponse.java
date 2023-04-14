package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseSetting 对象", title = "案例问卷设置Response")
public class CaseSettingResponse {
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例问卷设置ID")
    private String caseSettingId;

    @Schema(title = "记分方式[少选不得分|少选得一半分]")
    private String scoreMode;

    @Schema(title = "分配方式")
    private String allotMode;

    @Schema(title = "额外配置[JSON]")
    private String ext;


}
