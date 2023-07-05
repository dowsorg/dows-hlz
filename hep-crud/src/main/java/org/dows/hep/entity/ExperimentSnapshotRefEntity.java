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
 * @author : wuzl
 * @date : 2023/6/28 16:05
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentSnapshotRef", title = "实验快照关联")
@TableName("experiment_snapshot_ref")
public class ExperimentSnapshotRefEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验关联ID")
    private String experimentSnapshotRefId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @Schema(title = "关联实验ID")
    private String refExperimentInstanceId;

    @Schema(title = "快照类型")
    private String snapshotType;

    @Schema(title = "源表名")
    private String srcTableName;

    @Schema(title = "md5")
    private String md5;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
