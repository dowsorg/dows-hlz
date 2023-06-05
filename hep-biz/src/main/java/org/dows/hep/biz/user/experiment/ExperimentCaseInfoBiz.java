package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.AllArgsConstructor;
import org.dows.hep.entity.ExperimentCaseInfoEntity;
import org.dows.hep.service.ExperimentCaseInfoService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * @author fhb
 * @description
 * @date 2023/5/31 17:52
 */
@AllArgsConstructor
@Service
public class ExperimentCaseInfoBiz {
    private final ExperimentCaseInfoService experimentCaseInfoService;

    /**
     * @author fhb
     * @description 获取社区背景
     * @date 2023/5/31 18:02
     * @param
     * @return
     */
    public String getDescr(String experimentInstanceId) {
        return getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getDescr, ExperimentCaseInfoEntity::getDescr);
    }

    /**
     * @author fhb
     * @description 获取社区帮助
     * @date 2023/5/31 18:02
     * @param
     * @return
     */
    public String getGuide(String experimentInstanceId) {
        return getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getGuide, ExperimentCaseInfoEntity::getGuide);
    }

    /**
     * @author fhb
     * @description 获取社区公告 嘎嘎写代码
     * @date 2023/5/31 18:03
     * @param
     * @return
     */
    public String getNotice(String experimentInstanceId) {
        return getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getNotice, ExperimentCaseInfoEntity::getNotice);
    }

    private String getCaseInfo(String experimentInstanceId, SFunction<ExperimentCaseInfoEntity, ?> sfunction, Function<ExperimentCaseInfoEntity, String> function) {
        return experimentCaseInfoService.lambdaQuery()
                .select(sfunction)
                .eq(ExperimentCaseInfoEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .map(function)
                .orElse("");
    }
}
