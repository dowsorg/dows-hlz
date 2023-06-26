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
 * 判断指标健康问题(IndicatorJudgeHealthProblem)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:17
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorJudgeHealthProblemRsEntity", title = "判断指标健康问题")
@TableName("experiment_indicator_judge_health_problem_rs")
public class ExperimentIndicatorJudgeHealthProblemRsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String experimentIndicatorJudgeHealthProblemId;

    @Schema(title = "分布式ID")
    private String indicatorJudgeHealthProblemId;

    @Schema(title = "实验id")
    private String experimentId;

    @Schema(title = "案例id")
    private String caseId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标功能ID")
    private String indicatorFuncId;

    @Schema(title = "健康问题名称")
    private String name;

    @Schema(title = "分数")
    private BigDecimal point;

    @Schema(title = "结果说明")
    private String resultExplain;

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

    @Schema(title = "目录id列表")
    private String indicatorCategoryIdArray;

    @Schema(title = "目录名称列表")
    private String indicatorCategoryNameArray;
}

