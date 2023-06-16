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
 * 指标基本信息与单一指标关系表(IndicatorViewBaseInfoSingle)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:32
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewBaseInfoSingleEntity", title = "指标基本信息单一指标")
@TableName("experiment_indicator_view_base_info_single")
public class ExperimentIndicatorViewBaseInfoSingleEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String experimentIndicatorViewBaseInfoSingleId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoSingleId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "指标名称")
    private String indicatorInstanceName;

    @Schema(title = "展示顺序")
    private Integer seq;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

