package org.dows.hep.entity;

import java.util.Date;

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

/**
 * 实验成绩报告(ExperimentGradeReport)实体类
 *
 * @author lait
 * @since 2023-04-18 13:55:30
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentGradeReport", title = "实验成绩报告")
@TableName("experiment_grade_report")
public class ExperimentGradeReportEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验成绩报告ID")
    private String experimentGradeReportId;

    @Schema(title = "实验案例报告ID")
    private String experimentCaseReportId;

    @Schema(title = "系统评分")
    private String systemScore;

    @Schema(title = "教师评分")
    private String teacherScore;

    @Schema(title = "评价")
    private String assess;

    @Schema(title = "账号名")
    private String accountName;

    @Schema(title = "账号ID")
    private String accountId;

    @Schema(title = "用户名")
    private String userName;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

