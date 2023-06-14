package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jx
 * @date 2023/6/5 16:02
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewPhysicalExam", title = "实验查看指标体格检查")
@TableName("experiment_indicator_view_physical_exam")
public class ExperimentIndicatorViewPhysicalExamEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验查看指标体格检查分布式ID")
    private String experimentJudgePhysicalExamId;

    @Schema(title = "教师端查看指标体格检查ID")
    private String indicatorViewPhysicalExamId;

    @Schema(title = "指标功能ID")
    private String experimentIndicatorFuncId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "体格检查名称")
    private String name;

    @Schema(title = "体格检查类别")
    private String experimentIndicatorCategoryId;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "结果解析")
    private String resultAnalysis;

    @Schema(title = "0-禁用，1-启用")
    private Integer status;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
