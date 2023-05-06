package org.dows.hep.api.tenant.casus.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fhb
 * @description
 * @date 2023/4/27 11:50
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseInstancePageResponse 对象", title = "案例Response")
public class CaseInstancePageResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例名称")
    private String caseName;

    @Schema(title = "案例图片")
    private String casePic;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "案例状态[0:发布|1:关闭]")
    private Integer state;

    @Schema(title = "创建时间")
    private Date dt;
}
