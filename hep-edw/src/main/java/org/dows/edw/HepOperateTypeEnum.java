package org.dows.edw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.edw.domain.*;
import org.dows.hep.api.edw.HepOperateType;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/12 15:23
 **/
@Getter
@AllArgsConstructor
public enum HepOperateTypeEnum /*implements HepOperateType*/ {
    HEP_FOLLOW_UP(HepFollowUp.class),
    HEP_FOOD_INTERVENE(HepFoodIntervene.class),
    HEP_HEALTH_EVALUATE(HepHealthEvaluate.class),
    HEP_HEALTH_EXAMINATION(HepHealthExamination.class),
    HEP_HEALTH_PROBLEM(HepHealthProblem.class),
    HEP_HEALTH_THERAPY(HepHealthTherapy.class),
    HEP_OPERATE_COST(HepOperateCost.class),
    HEP_PSYCHOLOGY_INTERVENE(HepPsychologyIntervene.class),
    HEP_SPORT_INTERVENE(HepSportIntervene.class);

    private final Class<?> clazz;


}
