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
 * 实验方案(ExperimentScheme)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:44
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentScheme", title = "实验方案")
@TableName("experiment_scheme")
public class ExperimentSchemeEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "方案提示")
    private String schemeTips;

    @Schema(title = "方案说明")
    private String schemeDescr;

    @Schema(title = "是否包含视频")
    private Integer containsVideo;

    @Schema(title = "视频题干")
    private String videoQuestion;

    @Schema(title = "方案状态[0:未提交,1:已提交]")
    private Integer state;

    @Schema(title = "方案设计")
    private String schemeSetting;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

