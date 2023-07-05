package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.FoodCookbookEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:28
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapFoodCookbook", title = "食谱快照")
@TableName("snap_food_cookbook")
public class SnapFoodCookbookEntity extends FoodCookbookEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;
}
