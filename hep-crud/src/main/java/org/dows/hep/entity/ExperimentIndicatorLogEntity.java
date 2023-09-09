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
 * @author : wuzl
 * @date : 2023/9/5 15:44
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentIndicatorLog", title = "实验指标历史记录")
@TableName("experiment_indicator_log")
public class ExperimentIndicatorLogEntity  implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "指标记录id")
    private String experimentIndicatorLogId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;


    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "指标id")
    private String experimentIndicatorId;

    @Schema(title = "指标名称")
    private String experimentIndicatorName;

    @Schema(title = "计算批次")
    private Integer evalNo;
    @Schema(title = "当前值")
    private String curVal;

    @Schema(title = "之前值")
    private String lastVal;

    @Schema(title = "期初值")
    private String periodInitVal;

    @Schema(title = "增量值")
    private BigDecimal changeVal;

    @Schema(title = "指标单位")
    private String unit;


    @Schema(title = "健康档案指标类型 1-健康指数 2-基本指标 3-热量")
    private Integer docType;


    @Schema(title = "计算天数")
    private Integer evalDay;

    @Schema(title = "计算时间")
    private Date evalTime;



    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;




}
