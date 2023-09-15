package org.dows.hep.biz.eval;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.api.enums.EnumIndicatorType;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/10 19:38
 */
@Component
@RequiredArgsConstructor
public class SportCalcBiz {


    private final EvalPersonCache evalPersonCache;
    private final static BigDecimal MULFactor4SportEnergy=new BigDecimal("0.0175");
    private final static BigDecimal DIVFactor4SportEnergy=new BigDecimal("7");


    public BigDecimal calcSportCostEnergy(String experimentPersonId, List<SportPlanItemVO> sportItems,boolean saveFlag){
        //单一项目运动消耗热量（kcal）=上期体重（kg）*运动时长（min）*运动强度（met）*运动频率（次/周）/7*0.0175
        //多个项目运动消耗热量为单一项目运动消耗热量之和
        if(ShareUtil.XObject.isEmpty(sportItems)){
            return BigDecimal.ZERO;
        }
        EvalPersonOnceHolder curHolder=evalPersonCache.getCurHolder(experimentPersonId);
        if(null==curHolder){
            return BigDecimal.ZERO;
        }

        String weight= curHolder.getSysIndicator(EnumIndicatorType.WEIGHT).getLastVal();
        BigDecimalOptional total=BigDecimalOptional.zero();

        sportItems.forEach(item->{
            total.add(BigDecimalOptional.valueOf(weight)
                    .mul(BigDecimalUtil.tryParseDecimal(item.getLastTime(), BigDecimal.ONE))
                    .mul(item.getStrengthMet())
                    .mul(BigDecimalUtil.tryParseDecimal(item.getFrequency(), BigDecimal.ONE))
                    .mul(MULFactor4SportEnergy)
                    .div(DIVFactor4SportEnergy,2,RoundingMode.HALF_UP)
                    .getValue());
        });
        BigDecimal rst= total.getValue(2, RoundingMode.HALF_UP);
        if(saveFlag) {
            curHolder.putCurVal(EnumIndicatorType.SPORT_ENERGY, BigDecimalUtil.formatDecimal(rst), true);
        }
        return rst;
    }
}
