package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.EventCategEntity;

/**
 * @author : wuzl
 * @date : 2023/7/3 14:53
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapEventCateg", title = "事件类别管理快照")
@TableName("snap_event_categ")
public class SnapEventCategEntity extends EventCategEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

}
