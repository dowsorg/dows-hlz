package org.dows.hep.biz.base.intervene;

import org.dows.hep.api.base.intervene.request.SaveFoodNutientRequest;
import org.dows.hep.api.base.intervene.response.FoodNutientResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:干预:饮食关键指标（营养成分）
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@Service
public class FoodNutientBiz{
    /**
    * @param
    * @return
    * @说明: 获取营养关键指标
    * @关联表: food_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public List<FoodNutientResponse> listFoodNutient() {
        return new ArrayList<FoodNutientResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 保存营养关键指标
    * @关联表: food_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean saveFoodNutient(SaveFoodNutientRequest saveFoodNutient ) {
        return Boolean.FALSE;
    }
}