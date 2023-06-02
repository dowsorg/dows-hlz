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
 * 学生机构操作记录(OperateOrgFunc)实体类
 *
 * @author lait
 * @since 2023-04-28 10:27:05
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

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;


    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;



    @Schema(title = "指标功能点id")
    private String indicatorFuncId;


    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "功能类型  1-基本信息 2-设置随访  3-开始随访 4-一般检查 11-健康问题 12-健康指导 13-疾病问题 14-健管目标 21-饮食干预 22-运动干预  23-自定义干预")
    private Boolean operateType;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "展示类型 0-不展示 1-用户端展示")
    private Integer reportFlag;

    @Schema(title = "展示标签")
    private String reportLabel;

    @Schema(title = "操作描述")
    private String reportDescr;

    @Schema(title = "剩余资金")
    private BigDecimal asset;

    @Schema(title = "消耗资金")
    private BigDecimal fee;

    @Schema(title = "报销资金")
    private BigDecimal refund;

    @Schema(title = "操作得分")
    private String score;

    @Schema(title = "操作时间")
    private Date operateTime;

    @Schema(title = "操作所在游戏内天数")
    private Integer operateGameDay;

    @Schema(title = "结算处理时间")
    private Date dealTime;

    @Schema(title = "结算所在游戏内天数")
    private Integer dealGameDay;

    @Schema(title = "状态")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

