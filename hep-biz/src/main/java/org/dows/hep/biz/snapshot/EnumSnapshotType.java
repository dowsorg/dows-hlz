package org.dows.hep.biz.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/6/27 13:59
 */
@AllArgsConstructor
@Getter
public enum EnumSnapshotType {
    NONE("","",0,false,"",""),
    CATEGIntervene("categ.intervene","干预类别",0,false,"intervene_category",""),
    CATEGEvent("categ.event","事件类别",0,false,"event_categ",""),
    FOODMaterial("food.material","食材",0,true,"food_material",""),
    FOODMaterialNutrient("food.material.nutrient","食材成分",0,false,"food_material_nutrient",""),
    FOODDishes("food.dishes","菜肴",0,true,"food_dishes",""),
    FOODDishesMaterial("food.dishes.material","菜肴明细",0,false,"food_dishes_material",""),
    FOODDishesNutrient("food.dishes.nutrient","菜肴成分",0,false,"food_dishes_nutrient",""),
    FOODCookbook("food.cookbook","菜谱",0,true,"food_cookbook",""),
    FOODCookbookDetail("food.cookbook.detail","菜谱明细",0,false,"food_cookbook_detail",""),
    FOODCookbookNutrient("food.cookbook.nutrient","菜谱成分",0,false,"food_cookbook_nutrient",""),
    SPORTItem("sport.item","运动项目",0,true,"sport_item",""),
    SPORTPlan("sport.plan"," 运动方案",0,true,"sport_plan",""),
    SPORTPlanItems("sport.plan.items"," 运动方案项目",0,false,"sport_plan_items",""),

    TreatItem("treat.item:","治疗项目",0,true,"treat_item",""),
    INDICATORInstance("indicator.instance","数据库指标",0,false,"indicator_instance",""),
    INDICATORRule("indicator.rule","数据库指标规则",0,false,"indicator_rule",""),
    CASEIndicatorInstance("case.indicator.instance","案例指标",0,false,"case_indicator_instance",""),
    CASEIndicatorExpression("case.indicator.expression","案例指标公式",0,false,"case_indicator_expression",""),
    CASEIndicatorExpressionItem("case.indicator.expression.item","案例指标公式",0,false,"case_indicator_expression_item",""),
    CASEIndicatorExpressionRef("case.indicator.expression.ref","案例指标公式",0,false,"case_indicator_expression_ref",""),
    CASEEvent("case.event","案例人物事件",0,false,"",""),

    ;
    private String code;
    private String name;

    private int writeOrder;

    private boolean filterState;

    private String srcTableName;
    private String dstTableName;

    public static EnumSnapshotType ofSrcTableName(String srcTableName){
        return Arrays.stream(EnumSnapshotType.values())
                .filter(i->i.getSrcTableName().equalsIgnoreCase(srcTableName))
                .findFirst()
                .orElse(EnumSnapshotType.NONE);
    }
}
