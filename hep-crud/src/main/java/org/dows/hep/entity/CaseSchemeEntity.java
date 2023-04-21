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
 * 案例方案(CaseScheme)实体类
 *
 * @author lait
 * @since 2023-04-21 10:30:20
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseScheme", title = "案例方案")
@TableName("case_scheme")
public class CaseSchemeEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "类别名")
    private String caseCategName;

    @Schema(title = "类别ID路径")
    private String caseCategIdPath;

    @Schema(title = "类别name路径")
    private String caseCategNamePath;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "方案提示")
    private String tips;

    @Schema(title = "方案说明")
    private String schemeDescr;

    @Schema(title = "是否包含视频[0-否|1-是]")
    private Boolean containsVideo;

    @Schema(title = "状态[0-关闭|1-开启]")
    private Boolean enabled;

    @Schema(title = "来源[admin|tenant]")
    private String source;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者Name")
    private String accountName;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "案例标示")
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

