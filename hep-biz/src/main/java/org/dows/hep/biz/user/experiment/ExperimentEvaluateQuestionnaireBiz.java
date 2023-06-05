package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.evaluate.request.EvaluateQuestionnaireSearchRequest;
import org.dows.hep.api.base.evaluate.response.EvaluateQuestionnaireResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.entity.EvaluateQuestionnaireEntity;
import org.dows.hep.service.EvaluateQuestionnaireService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Service
public class ExperimentEvaluateQuestionnaireBiz {
    private final EvaluateQuestionnaireService evaluateQuestionnaireService;

    /**
     * @author fhb
     * @description 列出案例问卷
     * @date 2023/6/5 17:19
     * @param
     * @return
     */
    public List<EvaluateQuestionnaireResponse> listEvaluateQuestionnaire(EvaluateQuestionnaireSearchRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        List<String> categIds = request.getCategIds();
        String categId = CollUtil.isEmpty(categIds) ? null : categIds.get(0);
        String appId = request.getAppId();
        String keyword = request.getKeyword();
        List<EvaluateQuestionnaireEntity> questionnaireList = evaluateQuestionnaireService.lambdaQuery()
                .eq(StrUtil.isNotBlank(categId), EvaluateQuestionnaireEntity::getEvaluateCategId, categId)
                .eq(StrUtil.isNotBlank(appId), EvaluateQuestionnaireEntity::getAppId, appId)
                .eq(StrUtil.isNotBlank(keyword), EvaluateQuestionnaireEntity::getEvaluateQuestionnaireName, keyword)
                .list();
        if (CollUtil.isEmpty(questionnaireList)) {
            return new ArrayList<>();
        }
        return BeanUtil.copyToList(questionnaireList, EvaluateQuestionnaireResponse.class);
    }

    /**
     * @author fhb
     * @description
     * @date 2023/6/3 21:02
     * @param
     * @return
     */
    public Boolean submitQuestionnaire(ExperimentQuestionnaireRequest request) {
        return null;
    }
}
