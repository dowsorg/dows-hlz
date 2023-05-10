package org.dows.hep.biz.base.intervene;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.SaveFoodNutientRequest;
import org.dows.hep.api.base.intervene.response.FoodNutientResponse;
import org.dows.hep.biz.dao.IndicatorInstanceDao;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.IndicatorInstanceEntity;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description project descr:干预:饮食关键指标（营养成分）
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class FoodNutientBiz{
    private final IndicatorInstanceDao dao;

    /**
    * @param
    * @return
    * @说明: 获取营养关键指标
    * @关联表: food_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<FoodNutientResponse> listFoodNutient() {
        List<IndicatorInstanceEntity> rows = dao.getIndicators4Nutrient(IndicatorInstanceEntity::getId,
                IndicatorInstanceEntity::getIndicatorInstanceId,
                IndicatorInstanceEntity::getIndicatorName,
                IndicatorInstanceEntity::getUnit);
        return ShareUtil.XCollection.map(rows, true, i ->
                CopyWrapper.create(FoodNutientResponse::new).endFrom(i,v->v.setNutrientName(i.getIndicatorName())));
    }
    /**
    * @param
    * @return
    * @说明: 保存营养关键指标
    * @关联表: food_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveFoodNutient(SaveFoodNutientRequest saveFoodNutient ) {
        return Boolean.FALSE;
    }
}