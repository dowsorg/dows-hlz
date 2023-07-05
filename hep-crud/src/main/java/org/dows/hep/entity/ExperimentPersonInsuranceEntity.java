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
 * @author jx
 * @date 2023/6/27 16:59
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPersonInsurance", title = "保险购买记录表")
@TableName("experiment_person_insurance")
public class ExperimentPersonInsuranceEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验人物保险ID")
    private String experimentPersonInsuranceId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验期数")
    private String periods;

    @Schema(title = "购买保险机构ID")
    private String operateOrgId;

    @Schema(title = "保险金额")
    private BigDecimal insuranceAmount;

    @Schema(title = "报销比例")
    private Double reimburseRatio;

    @Schema(title = "保险生效时间")
    private Date indate;

    @Schema(title = "保险失效时间")
    private Date expdate;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
