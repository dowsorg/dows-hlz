package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description 问题域类目均由此管理
 * @date 2023/4/19 16:39
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionCategoryRequest 对象", title = "问题域类目Request")
public class QuestionCategoryRequest {

    @Schema(title = "类别ID")
    private String questionCategId;

    @Schema(title = "类别父id")
    @NotBlank(message = "类别父id不能为空")
    private String questionCategPid;

    @Schema(title = "类别组")
    @NotBlank(message = "类别组不能为空")
    private String questionCategGroup;

    @Schema(title = "类别名")
    @NotBlank(message = "类别名不能为空")
    private String questionCategName;

    @Schema(title = "序列号")
    private Integer sequence;


    // JsonIgnore
    @Schema(title = "类别ID路径")
    @JsonIgnore
    private String questionCategIdPath;

    @Schema(title = "类别name路径")
    @JsonIgnore
    private String questionCategNamePath;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    @JsonIgnore
    private Long id;
}
