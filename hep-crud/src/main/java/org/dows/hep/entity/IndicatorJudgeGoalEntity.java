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

import java.util.Date;

/**
 * 管理目标
 *
 * @author : wuzl
 * @date : 2023/10/17 23:03
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "IndicatorJudgeGoal", title = "管理目标")
@TableName("indicator_judge_goal")
public class IndicatorJudgeGoalEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "管理目标id")
    private String indicatorJudgeGoalId;

    @Schema(title = "管理目标名称")
    private String indicatorJudgeGoalName;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "分类ID")
    private String categId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;





    @Schema(title = "状态 0-停用 1-启用")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}
