package org.dows.hep.entity;

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

import java.util.Date;

/**
 * 问题集[试卷](QuestionSection)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:47
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionSection", title = "问题集[试卷]")
@TableName("question_section")
public class QuestionSectionEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "类别ID")
    private String questionSectionCategId;

    @Schema(title = "类别名")
    private String questionSectionCategName;

    @Schema(title = "类别ID路径")
    private String questionSectionCategIdPath;

    @Schema(title = "类别name路径")
    private String questionSectionCategNamePath;

    @Schema(title = "问题集名称")
    private String name;

    @Schema(title = "问题集提示")
    private String tips;

    @Schema(title = "问题集说明")
    private String descr;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "来源")
    private String source;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "权限[000001]")
    private String permissions;

    @Schema(title = "题数")
    private Integer questionCount;

    @Schema(title = "题型结构")
    private String questionSectionStructure;

    @Schema(title = "引用计数")
    private Integer refCount;

    @Schema(title = "问题集标识")
    private String questionSectionIdentifier;

    @Schema(title = "版本号")
    private String ver;

    @Schema(title = "状态")
    private Boolean enabled;

    @Schema(title = "biz code")
    private String bizCode;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

