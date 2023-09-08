package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/9/5 14:54
 */
@Data
@Accessors(chain = true)
public class EvalIndicatorValues {

    @Schema(title = "指标id")
    private String indicatorId;

    @Schema(title = "指标名称")
    private String indicatorName;

    @Schema(title = "计算批次")
    private Integer evalNo;
    @Schema(title = "当前值")
    private String curVal;
    @Schema(title = "之前值")
    private String lastVal;
    @Schema(title = "期初值")
    private String periodInitVal;
    @Schema(title = "增量值")
    private BigDecimal changingVal;
    @Schema(title = "已结算增量值")
    private BigDecimal changedVal;

    public boolean isChanged() {
        return ShareUtil.XObject.notEmpty(changingVal)
                ||!Objects.equals(curVal, lastVal);

    }

    public EvalIndicatorValues setSynced(){
        changedVal=changingVal;
        changingVal=null;
        return this;
    }


    public EvalIndicatorValues flip(boolean isPeriodInit){
        return new EvalIndicatorValues()
                .setIndicatorId(indicatorId)
                .setIndicatorName(indicatorName)
                .setEvalNo(evalNo)
                .setCurVal(curVal)
                .setLastVal(curVal)
                .setPeriodInitVal(isPeriodInit?curVal:periodInitVal)
                .setChangingVal(null)
                .setChangedVal(null);
    }

}
