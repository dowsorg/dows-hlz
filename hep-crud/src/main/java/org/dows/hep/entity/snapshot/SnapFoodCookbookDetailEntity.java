package org.dows.hep.entity.snapshot;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.entity.FoodCookbookDetailEntity;

/**
 * @author : wuzl
 * @date : 2023/7/2 13:29
 */
@Data
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SnapFoodCookbookDetail", title = "食谱食材快照")
@TableName("snap_food_cookbook_detail")
public class SnapFoodCookbookDetailEntity extends FoodCookbookDetailEntity implements ExperimentCrudEntity {

    @Schema(title = "实验ID")
    private String experimentInstanceId;
}
