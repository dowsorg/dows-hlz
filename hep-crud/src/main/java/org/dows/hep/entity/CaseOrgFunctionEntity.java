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
 * 机构功能(CaseOrgFunction)实体类
 *
 * @author lait
 * @since 2023-04-21 19:41:29
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseOrgFunction", title = "机构功能")
@TableName("case_org_function")
public class CaseOrgFunctionEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "功能|菜单名称")
    private String functionName;

    @Schema(title = "功能图标")
    private String functionIcon;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "版本号")
    private String ver;

    @Schema(title = "案例标示")
    private String caseIdentifier;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

