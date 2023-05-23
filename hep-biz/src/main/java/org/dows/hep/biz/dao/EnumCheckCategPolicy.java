package org.dows.hep.biz.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.enums.EnumCategFamily;

/**
 * 类别引用监测策略
 *
 * @author : wuzl
 * @date : 2023/5/11 9:51
 */
@AllArgsConstructor
@Getter
public enum EnumCheckCategPolicy {
    FOODMaterial(EnumCategFamily.FOODMaterial,FoodMaterialDao.class),

    FOODDishes(EnumCategFamily.FOODDishes,FoodDishesDao.class ),
    FOODCookBook(EnumCategFamily.FOODCookBook,FoodCookbookDao.class ),

    SPORTItem(EnumCategFamily.SPORTItem,SportItemDao.class),
    SPORTPlan(EnumCategFamily.SPORTPlan, SportPlanDao.class),

    TreatItem(EnumCategFamily.TreatItem, TreatItemDao.class),

    EVENT(EnumCategFamily.EVENT,EventDao.class),
    ;
    private EnumCategFamily categFamily;
    private Class<? extends ICheckCategRef> checkClazz;

    public boolean checkCategRef(String categId) {
        ICheckCategRef dao = CrudContextHolder.getBean(checkClazz);
        return null == dao ? false : dao.checkCategRef(categId);
    }
    public static boolean checkCategRef(EnumCategFamily family,String categId){
        EnumCheckCategPolicy policy=of(family);
        return null==policy?false:policy.checkCategRef(categId);
    }
    public static boolean checkCategRef(String family,String categId){
        EnumCheckCategPolicy policy=of(family);
        return null==policy?false:policy.checkCategRef(categId);
    }

    public static EnumCheckCategPolicy of(EnumCategFamily family){
        for(EnumCheckCategPolicy item: EnumCheckCategPolicy.values()){
            if(item.getCategFamily().equals(family)){
                return item;
            }
        }
        return null;
    }
    public static EnumCheckCategPolicy of(String family){
        EnumCategFamily enumCateg=EnumCategFamily.of(family);
        return null==enumCateg?null:of(enumCateg);
    }
}
