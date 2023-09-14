package org.dows.calc;

import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.calc.Calculatable;
import org.dows.hep.api.calc.ExperimentScoreCalcRequest;
import org.dows.hep.api.enums.EnumCalcCode;
import org.dows.hep.api.exception.ExperimentException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * 排行榜计算
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CalculatorDispatcher {

    private final Map<String, Calculatable> calculatableMap;

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;


    public void calc(ExperimentScoreCalcRequest experimentScoreCalcRequest) {
        List<EnumCalcCode> enumCalcCodes = experimentScoreCalcRequest.getEnumCalcCodes();
        if (enumCalcCodes == null || enumCalcCodes.size() == 0) {
            throw new ExperimentException("实验分数计算器集合为空");
        }

        extracted(experimentScoreCalcRequest.getExperimentInstanceId(),
                experimentScoreCalcRequest.getExperimentGroupId(),
                experimentScoreCalcRequest.getPeriod(),
                enumCalcCodes);
    }


    public void calc(String expeirmentInstanceId, String experimentGroupId, Integer peroid, List<EnumCalcCode> calcCodes) {
        extracted(expeirmentInstanceId, experimentGroupId, peroid, calcCodes);
    }


    private void extracted(String expeirmentInstanceId,
                           String experimentGroupId,
                           Integer peroid,
                           List<EnumCalcCode> enumCalcCodes) {
        // enumCalcCodes 为空不执行
        if (CollUtil.isEmpty(enumCalcCodes)) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(enumCalcCodes.size());
        for (EnumCalcCode enumCalcCode : enumCalcCodes) {
            Calculatable calculatable = calculatableMap.get(enumCalcCode.name());
            if (calculatable == null) {
                latch.countDown();
                continue;
            }

            threadPoolTaskExecutor.execute(() -> {
                // 线程执行主逻辑
                calculatable.calc(expeirmentInstanceId, experimentGroupId, peroid);
                // 任务执行完毕，减少 CountDownLatch 的计数
                latch.countDown();
            });
        }

        // 等待所有handler线程执行完毕
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
