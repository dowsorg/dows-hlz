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
 * 学生操作指标记录表(OperateIndictar)实体类
 *
 * @author lait
 * @since 2023-04-23 09:47:03
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateIndictar", title = "学生操作指标记录表")
@TableName("operate_indictar")
public class OperateIndictarEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "学生操作指标记录表ID")
    private String operateIndictarId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "操作人ID")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "案例人物")
    private String caseAccountId;

    @Schema(title = "案例人名")
    private String caseAccountName;

    @Schema(title = "操作[干预]类型")
    private String operateType;

    @Schema(title = "干预或事件处理id")
    private String operateSourceId;

    @Schema(title = "指标ID")
    private String indactorInstanceId;

    @Schema(title = "指标名称")
    private String indactorName;

    @Schema(title = "指标")
    private String indactorCode;

    @Schema(title = "记录原值")
    private String indactorOrgVal;

    @Schema(title = "记录变化值")
    private String indactorIncVal;

    @Schema(title = "记录最终值")
    private String indactorVal;

    @Schema(title = "指标单位")
    private String indactorUnit;

    @Schema(title = "期数")
    private Integer periods;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

