package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * 实验机构操作类型
 *
 * @author : wuzl
 * @date : 2023/4/21 13:48
 */
@AllArgsConstructor
@Getter
public enum EnumExptOperateType {
    NONE(0,null,"NA",-1,false,0,false),

    VIEWBaseInfo(1,EnumIndicatorCategory.VIEW_MANAGEMENT_BASE_INFO,"查看基本信息",-1,false,1,false),
    VIEWFollowUp(2,EnumIndicatorCategory.VIEW_MANAGEMENT_MONITOR_FOLLOWUP,"监测随访",-1,true,2,false),
    VIEWTwoLevel(3,EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_TWO_LEVEL,"二级无报告",-1,true,3,false),
    VIEWFourLevel(4,EnumIndicatorCategory.VIEW_MANAGEMENT_NO_REPORT_FOUR_LEVEL,"四级无报告",-1,true,4,false),

    INTERVENEFood(11,EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_DIET,"饮食干预",-1,false,11,false),
    INTERVENESport(12,EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_SPORTS,"运动干预",-1,false,12,false),
    INTERVENETreatTwoLevel(13,EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_PSYCHOLOGY,"心理治疗-二级无报告",1,true,13,false),
    INTERVENETreatFourLevel(14,EnumIndicatorCategory.OPERATE_MANAGEMENT_INTERVENE_TREATMENT,"药物治疗-四级有报告",3,true,14,true),
    JUDGERiskFactor(21,EnumIndicatorCategory.JUDGE_MANAGEMENT_RISK_FACTOR,"危险因素-二级无报告",-1,false,21,false),
    JUDGEHealthProblem(22,EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_PROBLEM,"健康问题-三级无报告",-1,true,22,false),
    JUDGEDiseaseProblem(23,EnumIndicatorCategory.JUDGE_MANAGEMENT_DISEASE_PROBLEM,"疾病问题-四级无报告",-1,true,23,false),
    JUDGEHealthGuidance(24,EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_GUIDANCE,"健康指导-二级有报告",-1,false,24,true),
    JUDGEHealthGoal(25,EnumIndicatorCategory.JUDGE_MANAGEMENT_HEALTH_MANAGEMENT_GOAL,"健管目标",-1,false,25,false),
    ;
    private Integer code;

    private EnumIndicatorCategory indicatorCateg;
    private String name;
    /**
     * 类别层级数
     */
    private Integer categLayer;
    /**
     * 是否记录服务记录
     */
    private Boolean reportFuncFlag;
    /**
     * 生成汇总报告顺序，=0不参与
     */
    private Integer reportFlowSeq;

    /**
     * 是否终止挂号流程
     */
    private Boolean endFlag;

    public Boolean getReportFlowFlag(){
        return reportFlowSeq>0;
    }
    public static EnumExptOperateType ofCategId(String categId){
        EnumIndicatorCategory categ= Arrays.stream(EnumIndicatorCategory.values())
                .filter(i->i.getCode().equalsIgnoreCase(categId))
                .findFirst()
                .orElse(null);
        if(null==categ){
            return EnumExptOperateType.NONE;
        }
        return Arrays.stream(EnumExptOperateType.values())
                .filter(i->categ.equals(i.getIndicatorCateg()))
                .findFirst()
                .orElse(EnumExptOperateType.NONE);
    }







}
