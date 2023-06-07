package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptQuestionnaireStateEnum;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 20:43
 */
@AllArgsConstructor
@Service
public class ExperimentQuestionnaireBiz {
    private final ExperimentQuestionnaireService experimentQuestionnaireService;
    private final ExperimentQuestionnaireItemBiz experimentQuestionnaireItemBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/6/3 20:53
     */
    public ExperimentQuestionnaireResponse getQuestionnaire(String experimentInstanceId, String periods, String experimentOrgId, String experimentGroupId, String experimentAccountId) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(periods, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentOrgId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentGroupId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentAccountId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 根据实验id、期数、小组id， 机构id 获取知识答题
        ExperimentQuestionnaireEntity entity = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentAccountId)
                .eq(ExperimentQuestionnaireEntity::getPeriods, periods)
                .eq(ExperimentQuestionnaireEntity::getExperimentOrgId, experimentOrgId)
                .eq(ExperimentQuestionnaireEntity::getExperimentGroupId, experimentAccountId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.QUESTIONNAIRE_NOT_NULL));
        ExperimentQuestionnaireResponse result = BeanUtil.copyProperties(entity, ExperimentQuestionnaireResponse.class);

        List<ExperimentQuestionnaireItemResponse> itemList = experimentQuestionnaireItemBiz.listByQuestionnaireId(entity.getExperimentQuestionnaireId());
        result.setItemList(itemList);

        return result;
    }

    /**
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     * @param
     * @return
     */
    public Boolean updateScheme(ExperimentQuestionnaireRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        checkState(request.getExperimentQuestionnaireId());

        List<ExperimentQuestionnaireItemRequest> itemList = request.getItemList();
        return experimentQuestionnaireItemBiz.updateBatch(itemList);
    }

    /**
     * @param
     * @return
     * @说明: 提交
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean submitScheme(String experimentQuestionnaireId) {
        if (StrUtil.isBlank(experimentQuestionnaireId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // check
        checkState(experimentQuestionnaireId);

        // submit
        boolean updateRes = experimentQuestionnaireService.lambdaUpdate()
                .eq(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, experimentQuestionnaireId)
                .set(ExperimentQuestionnaireEntity::getState, ExptQuestionnaireStateEnum.SUBMITTED.getCode())
                .update();

        // todo compute

        return updateRes;
    }

    private void checkState(String experimentQuestionnaireId) {
        ExperimentQuestionnaireEntity questionnaireEntity = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, experimentQuestionnaireId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
        Integer state = questionnaireEntity.getState();
        if (Objects.equals(ExptQuestionnaireStateEnum.SUBMITTED.getCode(), state)) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
    }
}
