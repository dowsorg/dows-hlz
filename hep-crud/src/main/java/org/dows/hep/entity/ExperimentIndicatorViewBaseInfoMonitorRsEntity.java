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
 * 指标基本信息监测表(IndicatorViewBaseInfoMonitor)实体类
 *
 * @author lait
 * @since 2023-04-28 10:26:27
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorViewBaseInfoMonitorRsEntity", title = "指标基本信息监测表")
@TableName("experiment_indicator_view_base_info_monitor_rs")
public class ExperimentIndicatorViewBaseInfoMonitorRsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String experimentIndicatorViewBaseInfoMonitorId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoMonitorId;

    @Schema(title = "实验id")
    private String experimentId;

    @Schema(title = "案例id")
    private String caseId;

    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标基本信息监测表名称")
    private String name;

    @Schema(title = "展示顺序")
    private Integer seq;

    @Schema(title = "基本信息监测随访随访内容名称")
    private String ivbimContentNameArray;

    @Schema(title = "基本信息监测随访随访内容指标id, #号分隔不同随访内容，','分隔单个随访内容")
    private String ivbimContentRefIndicatorInstanceIdArray;

    @Schema(title = "基本信息监测随访随访内容指标名称, #号分隔不同随访内容，','分隔单个随访内容")
    private String ivbimContentRefIndicatorInstanceNameArray;


    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

