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

@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentSchemeItem", title = "实验方案item")
@TableName("experiment_scheme_item")
public class ExperimentSchemeItemEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "item Id")
    private String experimentSchemeItemId;

    @Schema(title = "item pid")
    private String experimentSchemeItemPid;

    @Schema(title = "方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "问题解析")
    private String questionDetailedAnswer;

    @Schema(title = "作答人账号ID")
    private String accountId;

    @Schema(title = "排序")
    private Integer seq;

    @Schema(title = "答题结果")
    private String questionResult;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
