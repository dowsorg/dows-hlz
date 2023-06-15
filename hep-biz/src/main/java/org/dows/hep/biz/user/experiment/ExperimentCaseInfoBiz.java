package org.dows.hep.biz.user.experiment;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.AllArgsConstructor;
import org.dows.hep.api.tenant.casus.CasePeriodEnum;
import org.dows.hep.biz.tenant.experiment.ExperimentCaseInfoManageBiz;
import org.dows.hep.entity.ExperimentCaseInfoEntity;
import org.dows.hep.service.ExperimentCaseInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
    public ExperimentCaseInfoManageBiz.CaseNotice getNotice(String experimentInstanceId, CasePeriodEnum period) {
        String caseInfo = getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getNotice, ExperimentCaseInfoEntity::getNotice);
        Map periodMap = JSONUtil.toBean(caseInfo, HashMap.class);
        JSONObject object = (JSONObject) periodMap.get(String.valueOf(period.ordinal()));
        return JSONUtil.toBean(object, ExperimentCaseInfoManageBiz.CaseNotice.class);
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
