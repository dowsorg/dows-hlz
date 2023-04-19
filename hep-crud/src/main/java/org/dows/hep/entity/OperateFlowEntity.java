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
import  org.dows.framework.crud.mybatis.CrudEntity;

/**
 * 实验操作流程(OperateFlow)实体类
 *
 * @author lait
 * @since 2023-04-18 13:58:43
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateFlow", title = "实验操作流程")
@TableName("operate_flow")
public class OperateFlowEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验操作流程")
    private String operateFlowId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例账号ID")
    private String caseAccountId;

    @Schema(title = "流程名称")
    private String flowName;

    @Schema(title = "流程顺序")
    private String flowSequence;

    @Schema(title = "操作记录")
    private String recordJson;

    @Schema(title = "状态")
    private Integer state;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

