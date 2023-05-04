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
 * 学生机构操作快照(OperateOrgFuncSnap)实体类
 *
 * @author lait
 * @since 2023-04-28 10:27:07
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OperateOrgFuncSnap", title = "学生机构操作快照")
@TableName("operate_org_func_snap")
public class OperateOrgFuncSnapEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验操作流程快照id")
    private String operateOrgFuncSnapId;

    @Schema(title = "机构操作id")
    private String operateOrgFuncId;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "快照时间")
    private Date snapTime;

    @Schema(title = "输入记录")
    private String inputJson;

    @Schema(title = "结果记录")
    private String resultJson;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

