package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.TreatItemEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:48
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapTreatItem", title = "治疗项目快照")
@TableName("snap_treat_item")
public class SnapTreatItemEntity extends TreatItemEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;

}
