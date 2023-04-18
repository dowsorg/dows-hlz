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
 * 实验人物资产花费(ExperimentPersonCost)实体类
 *
 * @author lait
 * @since 2023-04-18 13:55:51
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPersonCost", title = "实验人物资产花费")
@TableName("experiment_person_cost")
public class ExperimentPersonCostEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验花费ID")
    private String experimentPersonCostId;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "账号ID")
    private String caseAccountId;

    @Schema(title = "资产名称[医保,商保,养老]")
    private String assetName;

    @Schema(title = "资产额度[]")
    private Object assetAmount;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

