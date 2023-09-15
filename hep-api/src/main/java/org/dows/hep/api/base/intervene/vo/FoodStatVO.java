package org.dows.hep.api.base.intervene.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/5/6 9:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FoodStatVO 对象", title = "饮食统计")

public class FoodStatVO {
    @Schema(title = "主体(营养指标，食材分类)id")
    private String instanceId;

    @Schema(title = "主体名称")
    private String instanceName ;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "重量")
    private String weight;

    @Schema(title = "实际能量")
    private String energy;

    @Schema(title = "推荐量下限")
    private String min;

    @Schema(title = "推荐量上限")
    private String max;

    @Schema(title = "是否按照百分比展示")
    private Integer percentFlag;

    @Schema(title = "推荐量文本")
    private String rangeText;

    @Schema(title = "实际量文本")
    private String weightText;
    static final String CHARLink="-";
    static final String CHARPercent="%";

    public FoodStatVO buildRangeText(){
        StringBuilder sb=new StringBuilder();
        if(StringUtils.hasLength(this.min)){
            sb.append(this.min);
            if(null!=percentFlag&&percentFlag>0){
                sb.append(CHARPercent);
            }else if(StringUtils.hasLength(this.unit)) {
                sb.append(this.unit);
            }
        }
        sb.append(CHARLink);
        if(StringUtils.hasLength(this.max)){
            sb.append(this.max);
            if(null!=percentFlag&&percentFlag>0){
                sb.append(CHARPercent);
            }else if(StringUtils.hasLength(this.unit)) {
                sb.append(this.unit);
            }
        }
        rangeText= sb.toString();
        sb.setLength(0);
        return this;
    }
    public FoodStatVO buildWeightText(){
        if(!StringUtils.hasLength(this.weight)||weight.equals(CHARLink)){
            weightText=String.format("%s", "0", Optional.ofNullable(unit).orElse(""));
            return this;
        }
        weightText=String.format("%s%s", weight, Optional.ofNullable(unit).orElse(""));
        return this;
    }


}
