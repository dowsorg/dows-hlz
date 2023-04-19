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
import org.dows.framework.crud.mybatis.CrudEntity;

/**
 * 案例机构(CaseOrg)实体类
 *
 * @author lait
 * @since 2023-04-18 13:54:28
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseOrg", title = "案例机构")
@TableName("case_org")
public class CaseOrgEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "机构ID[uim域]")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "场景")
    private String scene;

    @Schema(title = "操作手册")
    private String handbook;

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

