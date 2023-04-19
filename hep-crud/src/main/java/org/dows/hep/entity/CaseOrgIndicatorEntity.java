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
 * 机构功能指标点(CaseOrgIndicator)实体类
 *
 * @author lait
 * @since 2023-04-18 13:54:29
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseOrgIndicator", title = "机构功能指标点")
@TableName("case_org_indicator")
public class CaseOrgIndicatorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "机构功能指标点ID")
    private String caseOrgIndicatorId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "指标分类ID")
    private String indicatorCategId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "指标Code")
    private String indicatorCode;

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

