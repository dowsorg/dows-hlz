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
 * 实验机构人物(ExperimentPerson)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:46
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPerson", title = "实验机构人物")
@TableName("experiment_person")
public class ExperimentPersonEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例机构名称")
    private String caseOrgName;

    @Schema(title = "账号ID")
    private String caseAccountId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "上个案例机构id")
    private String caseOrgIdLast;

    @Schema(title = "上个案例机构名称")
    private String caseOrgNameLast;

    @Schema(title = "挂号状态 0-未挂号 1-已挂号")
    private Boolean flowState;

    @Schema(title = "保险状态 0-未购买 1-已购买")
    private Boolean insuranceState;

    @Schema(title = "剩余资金")
    private Double asset;

    @Schema(title = "初始资金")
    private Double assetInit;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

