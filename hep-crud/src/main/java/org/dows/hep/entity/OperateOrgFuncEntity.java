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
 * 学生机构操作记录(OperateOrgFunc)实体类
 *
 * @author lait
 * @since 2023-04-21 10:30:24
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateOrgFunc", title = "学生机构操作记录")
@TableName("operate_org_func")
public class OperateOrgFuncEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "指标功能点id")
    private String indicatorFuncId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例账号ID")
    private String caseAccountId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "功能类型  1-基本信息 2-设置随访  3-开始随访 4-体格检查 5-辅助检查 11-健康问题 12-健康指导 13-疾病问题 14-健管目标 21-饮食干预 22-运动干预  23-心理干预 24-治疗方案")
    private Boolean funcType;

    @Schema(title = "展示类型 0-不展示 1-用户端展示")
    private Boolean reportFlag;

    @Schema(title = "消耗资金")
    private Double feeCost;

    @Schema(title = "剩余资金")
    private Double asset;

    @Schema(title = "操作得分")
    private String score;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作描述")
    private String operateDescr;

    @Schema(title = "标签")
    private String label;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

