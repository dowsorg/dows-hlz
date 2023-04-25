package org.dows.hep.api.base.intervene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/4/23 14:11
 */
@Data
@Builder
@Accessors(chain = true)
@Schema(name = "FoodCategExtendVO 对象", title = "食物类别扩展属性")
@NoArgsConstructor
@AllArgsConstructor
public class FoodCategExtendVO {

    @Schema(title = "单位")
    private String unit;
    @Schema(title = "推荐量下限")
    private String min;

    @Schema(title = "推荐量上限")
    private String max;
}