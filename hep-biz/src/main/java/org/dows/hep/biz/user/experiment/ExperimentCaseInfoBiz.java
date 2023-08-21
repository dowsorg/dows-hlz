package org.dows.hep.biz.user.experiment;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.AllArgsConstructor;
import org.dows.hep.api.user.experiment.dto.ExptCaseNoticeDTO;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.entity.ExperimentCaseInfoEntity;
import org.dows.hep.service.ExperimentCaseInfoService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.function.Function;

/**
 * @author fhb
 * @description
 * @date 2023/5/31 17:52
 */
@AllArgsConstructor
@Service
public class ExperimentCaseInfoBiz {
    private final ExperimentBaseBiz baseBiz;
    private final ExperimentCaseInfoService experimentCaseInfoService;
    private final ExperimentTimerBiz experimentTimerBiz;

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
    public ExptCaseNoticeDTO getNotice(String experimentInstanceId) {

        Integer currentPeriod= ShareBiz.getCurrentPeriod(null, experimentInstanceId);
        String caseInfo = getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getNotice, ExperimentCaseInfoEntity::getNotice);
        if (StrUtil.isBlank(caseInfo)) {
            return new ExptCaseNoticeDTO();
        }
        HashMap periodMap = JSONUtil.toBean(caseInfo, HashMap.class);
        JSONObject object = (JSONObject) periodMap.get(String.valueOf(currentPeriod));
        return JSONUtil.toBean(object, ExptCaseNoticeDTO.class);
    }

    /**
     * @author fhb
     * @description 获取知识答题得分模式 嘎嘎写代码
     * @date 2023/5/31 18:03
     * @param
     * @return
     */
    public String getQuestionnaireScoreMode(String experimentInstanceId) {
        return getCaseInfo(experimentInstanceId, ExperimentCaseInfoEntity::getScoreMode, ExperimentCaseInfoEntity::getScoreMode);
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
