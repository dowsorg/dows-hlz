package org.dows.hep.api.calc;

import org.dows.hep.api.calc.ExperimentScoreCalcRequest;

/**
 * 计算接口
 */
public interface Calculatable {
    void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest);

    /**
     * 不传小组ID 则计算所有实验,不传期数则计算所有期数
     *
     * @param experimentInstanceId
     * @param experimentGroupId
     * @param period
     */
    void calc(String experimentInstanceId, String experimentGroupId, Integer period);

}
