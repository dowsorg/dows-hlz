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
import  org.dows.framework.crud.mybatis.CrudEntity;

/**
 * 问题类目(QuestionCategory)实体类
 *
 * @author lait
 * @since 2023-04-18 13:59:00
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionCategory", title = "问题类目")
@TableName("question_category")
public class QuestionCategoryEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "父ID")
    private Long pid;

    @Schema(title = "类别父id")
    private String questionCategPid;

    @Schema(title = "类别ID")
    private String questionCategId;

    @Schema(title = "类别名")
    private String questionCategName;

    @Schema(title = "类别ID路径")
    private String questionCategIdPath;

    @Schema(title = "类别name路径")
    private String questionCategNamePath;

    @Schema(title = "类别组")
    private String questionCategGroup;

    @Schema(title = "序列号")
    private Integer sequence;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

