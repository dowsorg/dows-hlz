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
@Schema(name = "ExperimentViewBaseInfoSingleEntity", title = "实验查看指标基本信息单一指标表")
@TableName("experiment_view_base_info_single")
public class ExperimentViewBaseInfoSingleEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验指标基本信息单一指标分布式ID")
    private String experimentViewBaseInfoSingleId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "教师端查看指标基本信息功能点ID")
    private String indicatorViewBaseInfoId;

    @Schema(title = "实验指标ID")
    private String experimentIndicatorInstanceId;

    @Schema(title = "展示排序")
    private Integer Seq;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
