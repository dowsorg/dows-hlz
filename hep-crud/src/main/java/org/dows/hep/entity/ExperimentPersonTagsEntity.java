package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dows.framework.crud.api.CrudEntity;
import java.util.Date;

/**
 * @author jx
 * @date 2023/7/13 16:59
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentPersonTagsEntity", title = "实验人物标签")
@TableName("experiment_person_tags")
public class ExperimentPersonTagsEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验人物标签ID")
    private String experimentPersonTagsId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物标签ID")
    private String tagsId;

    @Schema(title = "实验人物标签名称")
    private String tagsName;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
