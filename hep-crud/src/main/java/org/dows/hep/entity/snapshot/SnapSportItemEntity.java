package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.SportItemEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:46
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapSportItem", title = "运动项目快照")
@TableName("snap_sport_item")
public class SnapSportItemEntity extends SportItemEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;

    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

}