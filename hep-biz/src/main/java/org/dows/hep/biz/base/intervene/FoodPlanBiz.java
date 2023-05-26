package org.dows.hep.biz.base.intervene;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.FoodCookBookInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodCookBookResponse;
import org.dows.hep.api.base.intervene.response.FoodDishesInfoResponse;
import org.dows.hep.api.base.intervene.response.FoodDishesResponse;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.FoodMaterialVO;
import org.dows.hep.api.base.intervene.vo.FoodStatVO;
import org.dows.hep.api.enums.EnumFoodDetailType;
import org.dows.hep.api.enums.EnumFoodMealTime;
import org.dows.hep.api.enums.EnumFoodNutrient;
import org.dows.hep.api.enums.EnumFoodStatType;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.FoodCookbookDao;
import org.dows.hep.biz.dao.FoodDishesDao;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.CalcFoodCookbookResult;
import org.dows.hep.biz.vo.CalcFoodDishesResult;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
* @description project descr:干预:饮食方案(菜肴菜谱)
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class FoodPlanBiz{

    private final FoodDishesDao daoDishes;

    private final FoodCookbookDao daoCookbook;

    private final FoodCalcBiz foodCalcBiz;

    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
    }




    /**
    * @param
    * @return
    * @说明: 获取菜肴列表
    * @关联表: food_dishes
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<FoodDishesResponse> pageFoodDishes(FindFoodRequest findFood ) {
        findFood.setCategIdLv1( getCategCache().getLeafIds(findFood.getCategIdLv1()));
        return ShareBiz.buildPage(daoDishes.pageByCondition(findFood), i-> CopyWrapper.create(FoodDishesResponse::new)
                .endFrom(refreshCateg(i))
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath() ,i.getCategName())));
    }
    /**
    * @param
    * @return
    * @说明: 获取菜肴信息
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FoodDishesInfoResponse getFoodDishes(String foodDishesId ) {
        FoodDishesEntity row= AssertUtil.getNotNull(daoDishes.getById(foodDishesId))
                .orElseThrow("菜肴不存在或已删除，请刷新");

        List<FoodDishesMaterialEntity> rowsMaterial= daoDishes.getSubByLeadId(foodDishesId);
        List<FoodDishesNutrientEntity> rowsNutrient= daoDishes.getSubByLeadIdX(foodDishesId);
        List<FoodMaterialVO> vosMaterial=ShareUtil.XCollection.map(rowsMaterial,
                i->CopyWrapper.create(FoodMaterialVO::new)
                        .endFrom(i,v->v.setRefId(i.getFoodDishesMaterialId())));
        List<FoodStatVO> vosEnergy=new ArrayList<>();
        List<FoodStatVO> vosCateg=new ArrayList<>();
        for(FoodDishesNutrientEntity item:rowsNutrient) {
            FoodStatVO vo = CopyWrapper.create(FoodStatVO::new).endFrom(item);
            switch (EnumFoodStatType.of(item.getInstanceType())) {
                case NUTRIENT:
                    vosEnergy.add(vo);
                    break;
                case FOODCateg:
                    vosCateg.add(vo);
                    break;
            }
        }
        return CopyWrapper.create(FoodDishesInfoResponse::new)
                .endFrom(row)
                .setMaterials(vosMaterial)
                .setStatEnergy(vosEnergy)
                .setStatCateg(vosCateg);
    }
    /**
    * @param
    * @return
    * @说明: 保存菜肴
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveFoodDishes(SaveFoodDishesRequest saveFoodDishes ) {
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveFoodDishes.getFoodDishesId())
                        && daoDishes.getById(saveFoodDishes.getFoodDishesId(), FoodDishesEntity::getFoodDishesId).isEmpty())
                .throwMessage("菜肴不存在或已删除，请刷新");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveFoodDishes.getInterveneCategId())
                        ||null==(categVO= getCategCache().getById(saveFoodDishes.getInterveneCategId())))
                .throwMessage("类别不存在");
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveFoodDishes.getMaterials())
                        &&saveFoodDishes.getMaterials().stream()
                        .map(FoodMaterialVO::getFoodMaterialId)
                        .collect(Collectors.toSet())
                        .size()<saveFoodDishes.getMaterials().size())
                .throwMessage("存在重复的食材，请检查");

        List<FoodDishesMaterialEntity> rowsMaterial=ShareUtil.XCollection.map(saveFoodDishes.getMaterials(),
                i->CopyWrapper.create(FoodDishesMaterialEntity::new).endFrom(i,v->v.setFoodDishesMaterialId(i.getRefId())));

        FoodDishesEntity row= CopyWrapper.create(FoodDishesEntity::new)
                .endFrom(saveFoodDishes)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setMaterialsDesc(calcMaterialsDesc(rowsMaterial));
        CalcFoodDishesResult calcRst = foodCalcBiz.calcFoodGraph4Dishes(rowsMaterial);
        Optional.ofNullable(calcRst.getStatEnergy()).ifPresent(i->i.forEach(v->{
            switch (EnumFoodNutrient.of(v.getInstanceName())){
                case PROTEIN:
                    row.setProtein(v.getWeight());
                    break;
                case FAT:
                    row.setFat(v.getWeight());
                    break;
                case CHO:
                    row.setCho(v.getWeight());
                    break;
                case ENERGY:
                    row.setEnergy(v.getWeight());
                    break;

            }
        }));
        return daoDishes.tranSave(row,rowsMaterial,calcRst.getNutrients());
    }
    /**
    * @param
    * @return
    * @说明: 删除菜肴
    * @关联表: food_dishes,food_dishes_material,food_dishes_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delFoodDishes(DelFoodDishesRequest delFoodDishes ) {
        return daoDishes.tranDelete(delFoodDishes.getIds(),true);
    }

    /**
     * 删除菜肴下的食材
     *
     * @param delRefItem
     * @return
     */
    public Boolean delFoodDishesRefItem(DelRefItemRequest delRefItem ) {
        return daoDishes.tranDeleteSub(delRefItem.getIds(),"食材不存在或已删除");
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用菜肴
    * @关联表: food_dishes
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setFoodDishesState(SetFoodDishesStateRequest setFoodDishesState ) {
        return daoDishes.tranSetState(setFoodDishesState.getFoodDishesId(), setFoodDishesState.getState());
    }
    /**
    * @param
    * @return
    * @说明: 获取菜谱列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<FoodCookBookResponse> pageFoodCookbook(FindFoodRequest findFood ) {
        findFood.setCategIdLv1(getCategCache().getLeafIds(findFood.getCategIdLv1()));
        return ShareBiz.buildPage(daoCookbook.pageByCondition(findFood), i-> CopyWrapper.create(FoodCookBookResponse::new)
                .endFrom(refreshCateg(i))
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath() ,i.getCategName())));

    }
    /**
    * @param
    * @return
    * @说明: 获取菜谱详细信息
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public FoodCookBookInfoResponse getFoodCookbook(String foodCookbookId ) {
        FoodCookbookEntity row= AssertUtil.getNotNull(daoCookbook.getById(foodCookbookId))
                .orElseThrow("食谱不存在或已删除，请刷新");

        List<FoodCookbookDetailEntity> rowsDetail= daoCookbook.getSubByLeadId(foodCookbookId);
        List<FoodCookbookNutrientEntity> rowsNutrient= daoCookbook.getSubByLeadIdX(foodCookbookId);
        List<FoodCookbookDetailVO> vosDetail=ShareUtil.XCollection.map(rowsDetail,i->
                CopyWrapper.create(FoodCookbookDetailVO::new)
                        .endFrom(i,v->v.setRefId(i.getFoodCookbookDetailId())));
        List<FoodStatVO> vosEnergy=new ArrayList<>();
        List<FoodStatVO> vosCateg=new ArrayList<>();
        for(FoodCookbookNutrientEntity item:rowsNutrient){
            FoodStatVO vo=CopyWrapper.create(FoodStatVO::new).endFrom(item);
            switch (EnumFoodStatType.of(item.getInstanceType())) {
                case NUTRIENT:
                    vosEnergy.add(vo);
                    break;
                case FOODCateg:
                    vosCateg.add(vo);
                    break;
            }
        }
        return CopyWrapper.create(FoodCookBookInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setDetails(vosDetail)
                .setStatEnergy(vosEnergy)
                .setStatCateg(vosCateg);
    }
    /**
    * @param
    * @return
    * @说明: 保存菜谱
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveFoodCookbook(SaveFoodCookbookRequest saveFoodCookbook ) {
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveFoodCookbook.getFoodCookbookId())
                        && daoCookbook.getById(saveFoodCookbook.getFoodCookbookId(), FoodCookbookEntity::getFoodCookbookId).isEmpty())
                .throwMessage("食谱不存在或已删除，请刷新");
        CategVO categVO = null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveFoodCookbook.getInterveneCategId())
                        || null == (categVO = getCategCache().getById(saveFoodCookbook.getInterveneCategId())))
                .throwMessage("类别不存在");
        saveFoodCookbook.setDetails(ShareUtil.XObject.defaultIfNull(saveFoodCookbook.getDetails(), Collections.emptyList()));
        Map<EnumFoodMealTime, List<String>> mapDetails = new HashMap<>();
        //按餐次校验重复食材或菜肴
        EnumFoodMealTime mealTime;
        EnumFoodDetailType detailType;
        for (FoodCookbookDetailVO item : saveFoodCookbook.getDetails()) {
            detailType=EnumFoodDetailType.of(item.getInstanceType());
            AssertUtil.trueThenThrow( EnumFoodDetailType.NONE==detailType)
                    .throwMessage(String.format("不存在的明细类型"));
            mealTime = EnumFoodMealTime.of(item.getMealTime());
            AssertUtil.trueThenThrow(mealTime == EnumFoodMealTime.NONE)
                    .throwMessage("不存在的餐次");
            //生成食材描述
            if(EnumFoodDetailType.MATERIAL==detailType){
                item.setMaterialsDesc(String.format("%s100g",item.getInstanceName()));
            }
            mapDetails.computeIfAbsent(mealTime, k -> new ArrayList<>()).add(item.getInstanceId());
        }
        mapDetails.forEach((k, v) -> {
            AssertUtil.trueThenThrow(v.stream().distinct().count() < v.size())
                    .throwMessage(String.format("%s存在重复的菜肴或食材", k.getName()));
        });
        mapDetails.clear();


        FoodCookbookEntity row = CopyWrapper.create(FoodCookbookEntity::new)
                .endFrom(saveFoodCookbook)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());
        List<FoodCookbookDetailEntity> rowsDetail = ShareUtil.XCollection.map(saveFoodCookbook.getDetails(),
                i -> CopyWrapper.create(FoodCookbookDetailEntity::new).endFrom(i, v -> v.setFoodCookbookDetailId(i.getRefId())));
        CalcFoodCookbookResult calcRst = foodCalcBiz.calcFoodGraph4Cookbook(rowsDetail);
        Optional.ofNullable(calcRst.getStatEnergy()).ifPresent(i->i.forEach(v->{
            switch (EnumFoodNutrient.of(v.getInstanceName())){
                case PROTEIN:
                    row.setProtein(v.getWeight());
                    break;
                case FAT:
                    row.setFat(v.getWeight());
                    break;
                case CHO:
                    row.setCho(v.getWeight());
                    break;
                case ENERGY:
                    row.setEnergy(v.getWeight());
                    break;

            }
        }));
        return daoCookbook.tranSave(row, rowsDetail, calcRst.getNutrients());
    }
    /**
    * @param
    * @return
    * @说明: 删除菜谱
    * @关联表: food_cookbook,food_cookbook_detail,food_cookbook_nutrient
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delFoodCookbook(DelFoodCookbookRequest delFoodCookbook ) {
        return daoCookbook.tranDelete(delFoodCookbook.getIds(),true);
    }

    /**
     * 删除菜谱下的食材菜肴
     * @param delRefItem
     * @return
     */
    public Boolean delFoodCookbookRefItem(DelRefItemRequest delRefItem ) {
        return daoCookbook.tranDeleteSub(delRefItem.getIds(),"食材/菜肴不存在或已删除");
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用菜谱
    * @关联表: food_cookbook
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setFoodCookbookState(SetFoodCookbookStateRequest setFoodCookbookState ) {
        return daoCookbook.tranSetState(setFoodCookbookState.getFoodCookbookId(), setFoodCookbookState.getState());
    }

    //region 获取类别缓存
    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected FoodDishesEntity refreshCateg(FoodDishesEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getInterveneCategId())) {
            return src;
        }
        CategVO cacheItem = getCategCache().getById(src.getInterveneCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath());

    }
    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected FoodCookbookEntity refreshCateg(FoodCookbookEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getInterveneCategId())) {
            return src;
        }
        CategVO cacheItem = getCategCache().getById(src.getInterveneCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath());

    }
    //endregion

    //region 计算相关
    //保留两位小数
    private static final int NUMBERScale2=2;
    /**
     * 计算菜肴每百克食材含量
     * @param details
     * @return
     */
    private String calcMaterialsDesc(List<FoodDishesMaterialEntity> details){
        if(ShareUtil.XCollection.isEmpty(details)){
            return "";
        }
        BigDecimalOptional total=BigDecimalOptional.create();
        for(FoodDishesMaterialEntity item:details){
            BigDecimal val= ShareBiz.fixDecimalWithScale(item.getWeight()) ;
            total.add(val);
            item.setWeight(BigDecimalUtil.formatDecimal(val));
        }
        StringBuilder sb=new StringBuilder();
        for(FoodDishesMaterialEntity item:details){
            if(sb.length()>0){
                sb.append(" ");
            }
            sb.append(String.format("%s%sg",item.getFoodMaterialName(),
                    BigDecimalOptional.valueOf(item.getWeight())
                            .mul(BigDecimalUtil.ONEHundred)
                            .div(total.getValue())
                            .getValue(NUMBERScale2)));
        }
        String rst=sb.toString();
        sb.setLength(0);
        return rst;

    }



    //endregion
}