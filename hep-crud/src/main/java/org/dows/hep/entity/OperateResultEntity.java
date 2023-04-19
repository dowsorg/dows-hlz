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
 * 操作结果(OperateResult)实体类
 *
 * @author lait
 * @since 2023-04-18 13:58:52
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateResult", title = "操作结果")
@TableName("operate_result")
public class OperateResultEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "操作结果ID")
    private String operateResultId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "操作账号ID")
    private String accountId;

    @Schema(title = "操作人员名称")
    private String accountName;

    @Schema(title = "操作ID")
    private String operateId;

    @Schema(title = "操作类型[答题,考试,事件...]")
    private String operateType;

    @Schema(title = "操作结果[答案]")
    private String operateResult;

    @Schema(title = "分数")
    private String score;

    @Schema(title = "期数")
    private Integer periods;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

