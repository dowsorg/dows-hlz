package org.dows.hep.biz.user.experiment;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.service.CaseSchemeService;
import org.springframework.stereotype.Service;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final ExperimentBaseBiz baseBiz;
    private final CaseSchemeService caseSchemeService;


    /**
     * @param
     * @return
     * @说明: 设计实验方案
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public CaseSchemeResponse getCaseScheme(String accountId, String experimentInstanceId) {
        if (StrUtil.isBlank(accountId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        String caseInstanceId = baseBiz.getCaseInstanceId(experimentInstanceId);
        // 组长可以获取所有的答案

        // 组员可以获取自己的答案
        return null;
    }

    /**
     * @param
     * @return
     * @说明: 提交方案
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean submitScheme(String schemeId) {
        return Boolean.FALSE;
    }
}