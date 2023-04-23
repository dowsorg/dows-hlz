package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;

import java.util.Date;

/**
 * 问题实例(QuestionInstance)实体类
 *
 * @author lait
 * @since 2023-04-18 13:59:06
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "QuestionInstance", title = "问题实例")
@TableName("question_instance")
public class QuestionInstanceEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "问题ID")
    @TableId(value = "questionInstance_id")
    private String questionInstanceId;

    @Schema(title = "父ID")
    private Long pid;

    @Schema(title = "问题pid")
    private String questionInstancePid;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "类别ID")
    private String questionCategId;

    @Schema(title = "类别名")
    private String questionCategName;

    @Schema(title = "类别ID路径")
    private String questionCategIdPath;

    @Schema(title = "类别name路径")
    private String questionCategNamePath;

    @Schema(title = "题目答题输入类型[input,select,text]")
    private String inputType;

    @Schema(title = "题目答题类型[单选|多选|判断|主观|材料]")
    private String questionType;

    @Schema(title = "维度ID")
    private String dimensionId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "状态")
    private Boolean enabled;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "来源")
    private String source;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者Name")
    private String accountName;

    @Schema(title = "权限[000001]")
    private String permissions;

    @Schema(title = "答案解析")
    private String detailedAnswer;

    @Schema(title = "引用计数")
    private Integer refCount;

    @Schema(title = "问题标识")
    private String questionIdentifier;

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

