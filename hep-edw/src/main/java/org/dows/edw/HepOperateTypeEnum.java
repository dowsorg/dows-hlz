package org.dows.edw;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.edw.domain.*;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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

    private final Class<? extends HepOperateEntity> clazz;

    private static final Map<Class<? extends HepOperateEntity>, HepOperateTypeEnum> cacheByCode;
    static {
        cacheByCode = Arrays.stream(HepOperateTypeEnum.values()).collect(Collectors.toMap(HepOperateTypeEnum::getClazz, item -> item));
    }

    public static HepOperateTypeEnum getByCode(Class<? extends HepOperateEntity> clazz) {
        return cacheByCode.get(clazz);
    }

    public static String getNameByCode(Class<? extends HepOperateEntity> clazz) {
        return cacheByCode.get(clazz).name();
    }

}
