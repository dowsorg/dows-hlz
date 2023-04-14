package org.dows.hep.biz.intervene;

import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.response.FoodNutientResponse;
import org.dows.hep.api.intervene.request.SaveFoodNutientRequest;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;;
/**
* @description project descr:干预:饮食关键指标（营养成分）
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
public class FoodNutientBiz{
    /**
    * @param
    * @return
    * @说明: 获取营养关键指标
    * @关联表: food_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 上午10:19:59
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
    * @创建时间: 2023年4月14日 上午10:19:59
    */
    public Boolean saveFoodNutient(SaveFoodNutientRequest saveFoodNutient ) {
        return Boolean.FALSE;
    }
}