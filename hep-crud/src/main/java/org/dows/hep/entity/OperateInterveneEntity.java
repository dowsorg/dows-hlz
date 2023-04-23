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
 * 学生干预操作记录(OperateIntervene)实体类
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
@Schema(name = "OperateIntervene", title = "学生干预操作记录")
@TableName("operate_intervene")
public class OperateInterveneEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String operateInterveneId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验操作流程")
    private String operateFlowId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "案例人物")
    private String caseAccountId;

    @Schema(title = "案例人名")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作[干预]类型 1-饮食 2-运动 3-心理 4-治疗")
    private String operateType;

    @Schema(title = "操作时间")
    private String operateTime;

    @Schema(title = "操作描述")
    private String descr;

    @Schema(title = "标签")
    private String tag;

    @Schema(title = "学生输入值json")
    private String operateValueJson;

    @Schema(title = "状态完整快照json")
    private Object operateContextJson;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

