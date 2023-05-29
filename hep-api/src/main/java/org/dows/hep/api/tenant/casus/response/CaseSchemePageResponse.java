package org.dows.hep.api.tenant.casus.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/4/26 15:44
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseSchemePageResponse 对象", title = "案例方案分页 Response")
public class CaseSchemePageResponse {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "类别名")
    private String caseCategName;

    @Schema(title = "创建者Name")
    private String accountName;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "状态[0-启用|1-关闭]")
    private Integer enabled;
}
