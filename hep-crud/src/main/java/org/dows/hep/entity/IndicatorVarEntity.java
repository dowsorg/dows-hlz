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
 * 指标变量(IndicatorVar)实体类
 *
 * @author lait
 * @since 2023-04-18 13:57:47
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "IndicatorVar", title = "指标变量")
@TableName("indicator_var")
public class IndicatorVarEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "分布式ID")
    private String indicatorVarId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "数据库名")
    private String dbName;

    @Schema(title = "表名")
    private String tbName;

    @Schema(title = "变量名")
    private String varName;

    @Schema(title = "变量code")
    private String varCode;

    @Schema(title = "期数，如果多期用[,]分割")
    private String periods;

    @Schema(title = "描述")
    private String descr;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

