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
 * @author jx
 * @date 2023/6/6 10:57
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentViewBaseInfoDescEntity", title = "实验查看指标基本信息描述表")
@TableName("experiment_view_base_info_desc")
public class ExperimentViewBaseInfoDescEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验指标基本信息指标描述表分布式ID")
    private String experimentViewBaseInfoDescId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "教师端查看指标基本信息功能点ID")
    private String indicatorViewBaseInfoId;

    @Schema(title = "指标基本信息描述表名称")
    private String name;

    @Schema(title = "描述表展示顺序")
    private Integer descDeq;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "指标展示顺序")
    private Integer indicatorSeq;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
