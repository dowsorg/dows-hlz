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
 * @date 2023/6/6 15:14
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentViewBaseInfoMonitorContentEntity", title = "实验查看指标基本信息监测内容表")
@TableName("experiment_view_base_info_monitor_content")
public class ExperimentViewBaseInfoMonitorContentEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验指标基本信息指标监测表内容分布式ID")
    private String experimentViewBaseInfoMonitorContentId;


    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验指标基本信息指标监测表分布式ID")
    private String experimentViewBaseInfoMonitorId;

    @Schema(title = "监测内容名称")
    private String name;

    @Schema(title = "展示排序")
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
