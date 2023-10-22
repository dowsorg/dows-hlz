package org.dows.hep.biz.snapshot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.hep.biz.util.ShareUtil;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/6/27 13:59
 */
@AllArgsConstructor
@Getter
public enum EnumSnapshotType {
    NONE("","",0,"","",""),
    CATEGIntervene("categ.intervene","干预类别",1,"","intervene_category",""),
    CATEGEvent("categ.event","事件类别",2,"","event_categ",""),
    FOODMaterial("food.material","食材",3,"state","food_material",""),
    FOODMaterialNutrient("food.material.nutrient","食材成分",4,"","food_material_nutrient",""),
    FOODDishes("food.dishes","菜肴",5,"state","food_dishes",""),
    FOODDishesMaterial("food.dishes.material","菜肴明细",6,"","food_dishes_material",""),
    FOODDishesNutrient("food.dishes.nutrient","菜肴成分",7,"","food_dishes_nutrient",""),
    FOODCookbook("food.cookbook","菜谱",8,"state","food_cookbook",""),
    FOODCookbookDetail("food.cookbook.detail","菜谱明细",9,"","food_cookbook_detail",""),
    FOODCookbookNutrient("food.cookbook.nutrient","菜谱成分",10,"","food_cookbook_nutrient",""),
    SPORTItem("sport.item","运动项目",11,"state","sport_item",""),
    SPORTPlan("sport.plan"," 运动方案",12,"state","sport_plan",""),
    SPORTPlanItems("sport.plan.items"," 运动方案项目",13,"","sport_plan_items",""),

    TreatItem("treat.item","治疗项目",14,"state","treat_item",""),

    INDICATORJudgeGoal("judge.goal","管理目标",21,"state","indicator_judge_goal",""),

    INDICATORJudgeHealthGuidance("judge.guidance","健康指导",22,"status","indicator_judge_health_guidance",""),

    INDICATORJudgeHealthProblem("judge.problem","健康问题",23,"status","indicator_judge_health_problem",""),

    INDICATORJudgeRiskFactor("judge.riskfactor","危险因素",24,"status","indicator_judge_risk_factor",""),


    INDICATORInstance("indicator.instance","数据库指标",31,"","indicator_instance",""),
    INDICATORRule("indicator.rule","数据库指标规则",32,"","indicator_rule",""),

    CROWD("crowd","人群",41,"","crowds_instance",""),
    RISKModel("crowd.riskmodel","风险模型",42,"status","risk_model",""),
    CASEIndicatorInstance("case.indicator.instance","案例指标",91,"","case_indicator_instance",""),

    CASEIndicatorExpressionRef("case.indicator.expression.ref","案例指标公式",93,"","case_indicator_expression_ref",""),
    CASEIndicatorExpression("case.indicator.expression","案例指标公式",94,"","case_indicator_expression",""),
    CASEIndicatorExpressionItem("case.indicator.expression.item","案例指标公式",95,"","case_indicator_expression_item",""),

    CASEEvent("case.event","案例人物事件",100,"","",""),

    ;
    private String code;
    private String name;

    private int writeOrder;

    private String colState;

    private String srcTableName;
    private String dstTableName;

    public String getColExperimentInstanceId(){
        return "experiment_instance_id";
    }

    public String getDstTableName(){
        if(ShareUtil.XObject.notEmpty(this.dstTableName)){
            return this.dstTableName;
        }
        return "snap_".concat(this.srcTableName);
    }

    public static EnumSnapshotType ofSrcTableName(String srcTableName){
        return Arrays.stream(EnumSnapshotType.values())
                .filter(i->i.getSrcTableName().equalsIgnoreCase(srcTableName))
                .findFirst()
                .orElse(EnumSnapshotType.NONE);
    }
}
