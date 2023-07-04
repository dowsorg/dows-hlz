package org.dows.hep.biz.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : wuzl
 * @date : 2023/6/27 13:59
 */
@AllArgsConstructor
@Getter
public enum EnumSnapshotType {
    NONE("","",0,""),
    CATEGIntervene("categ.intervene","干预类别",0,"intervene_category"),
    CATEGEvent("categ.event","事件类别",0,"event_categ"),
    FOODMaterial("food.material","食材",0,"food_material"),
    FOODMaterialNutrient("food.material.nutrient","食材成分",0,"food_material_nutrient"),
    FOODDishes("food.dishes","菜肴",0,"food_dishes"),
    FOODDishesMaterial("food.dishes.material","菜肴明细",0,"food_dishes_material"),
    FOODDishesNutrient("food.dishes.nutrient","菜肴成分",0,"food_dishes_nutrient"),
    FOODCookbook("food.cookbook","菜谱",0,"food_cookbook"),
    FOODCookbookDetail("food.cookbook.detail","菜谱明细",0,"food_cookbook_detail"),
    FOODCookbookNutrient("food.cookbook.nutrient","菜谱成分",0,"food_cookbook_nutrient"),
    SPORTItem("sport.item","运动项目",0,"sport_item"),
    SPORTPlan("sport.plan"," 运动方案",0,"sport_plan"),
    SPORTPlanItems("sport.plan.items"," 运动方案项目",0,"sport_plan_items"),

    TreatItem("treat.item:","治疗项目",0,"treat_item"),
    INDICATORInstance("indicator.instance","数据库指标",0,"indicator_instance"),
    INDICATORRule("indicator.rule","数据库指标规则",0,"indicator_rule"),
    CASEIndicatorInstance("case.indicator.instance","案例指标",0,"case_indicator_instance"),
    CASEIndicatorExpression("case.indicator.expression","案例指标公式",0,"case_indicator_expression"),
    CASEIndicatorExpressionItem("case.indicator.expression.item","案例指标公式",0,"case_indicator_expression_item"),
    CASEIndicatorExpressionRef("case.indicator.expression.ref","案例指标公式",0,"case_indicator_expression_ref"),
    CASEEvent("case.event","案例人物事件",0,"case_event"),

    ;
    private String code;
    private String name;

    private int writeOrder;

    private String srcTableName;
}
