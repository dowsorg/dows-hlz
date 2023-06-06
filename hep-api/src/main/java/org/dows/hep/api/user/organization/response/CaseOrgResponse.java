package org.dows.hep.api.user.organization.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/6 11:34
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseOrgResponse 对象", title = "案例机构")
public class CaseOrgResponse {

    @Schema(title = "数据库ID")
    private String id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "机构ID[uim域]")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "机构经度")
    private Float orgLongitude;

    @Schema(title = "机构纬度")
    private Float orgLatitude;

    @Schema(title = "操作手册")
    private String handbook;
}
