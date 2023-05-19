package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description
 * @date 2023/5/15 16:57
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseCategoryRequest 对象", title = "案例类目Request")
public class CaseCategoryRequest {
    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "类别父id")
    @NotBlank(message = "类别父id不能为空")
    private String caseCategPid;

    @Schema(title = "类别组")
    @NotBlank(message = "类别组不能为空")
    private String caseCategGroup;

    @Schema(title = "类别名")
    @NotBlank(message = "类别名不能为空")
    private String caseCategName;

    @Schema(title = "序列号")
    private Integer sequence;


    // JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    @JsonIgnore
    private Long id;
}
