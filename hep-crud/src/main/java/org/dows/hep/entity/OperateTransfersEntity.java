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
 * 操作机构转入转出记录(OperateTransfers)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:49
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateTransfers", title = "操作机构转入转出记录")
@TableName("operate_transfers")
public class OperateTransfersEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "操作机构转入转出记录ID")
    private String operateTransfersId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "转出机构ID")
    private String formOrgId;

    @Schema(title = "转出机构名称")
    private String formOrgName;

    @Schema(title = "转入机构ID")
    private String toOrgId;

    @Schema(title = "转入机构名称")
    private String toOrgName;

    @Schema(title = "案例人物ID")
    private String caseAccountId;

    @Schema(title = "案例人物名")
    private String caseAccountName;

    @Schema(title = "操作人员ID")
    private String operateAccountId;

    @Schema(title = "操作人员名称")
    private String operateAccountName;

    @Schema(title = "转入说明")
    private String descr;

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

