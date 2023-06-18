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
 * 指标基本信息描述表(IndicatorViewBaseInfoDescr)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:25
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewBaseInfoDescRsEntity", title = "指标基本信息描述表")
@TableName("experiment_indicator_view_base_info_desc_rs")
public class ExperimentIndicatorViewBaseInfoDescRsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String experimentIndicatorViewBaseInfoDescId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoDescId;

    @Schema(title = "实验id")
    private String experimentId;

    @Schema(title = "案例id")
    private String caseId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoId;

    @Schema(title = "指标基本信息描述表名称")
    private String name;

    @Schema(title = "展示顺序")
    private Integer seq;

    @Schema(title = "指标id列表")
    private String indicatorInstanceIdArray;

    @Schema(title = "指标名称列表")
    private String indicatorInstanceNameArray;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

