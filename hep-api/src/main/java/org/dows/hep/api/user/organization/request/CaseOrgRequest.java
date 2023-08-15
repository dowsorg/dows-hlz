package org.dows.hep.api.user.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/5/6 11:31
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CaseOrg 对象", title = "案例机构")
public class  CaseOrgRequest {

    @Schema(title = "数据库ID")
    private String id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "状态")
    private Integer status;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "机构ID[uim域]")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "场景")
    private String scene;

    @Schema(title = "操作手册")
    private String handbook;

    @Schema(title = "版本号")
    private String ver;

    @Schema(title = "案例标示")
    private String caseIdentifier;

    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;
}
