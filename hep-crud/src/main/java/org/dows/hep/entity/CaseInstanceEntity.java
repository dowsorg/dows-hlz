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
 * 案例实例(CaseInstance)实体类
 *
 * @author lait
 * @since 2023-04-18 13:54:27
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseInstance", title = "案例实例")
@TableName("case_instance")
public class CaseInstanceEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例名称")
    private String caseName;

    @Schema(title = "案例图片")
    private String casePic;

    @Schema(title = "案例类型")
    private String caseType;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "背景描述")
    private String descr;

    @Schema(title = "指导描述")
    private String guide;

    @Schema(title = "案例状态[0:未发布|1:发布]")
    private Integer state;

    @Schema(title = "案例唯一标示")
    private String caseIdentifier;

    @Schema(title = "版本号")
    private String ver;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

