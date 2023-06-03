package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.ExperimentSchemeService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class ExperimentSchemeManageBiz {
    private final IdGenerator idGenerator;

    private final TenantCaseSchemeBiz tenantCaseSchemeBiz;

    private final ExperimentSchemeService experimentSchemeService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 预生成方案设计试卷
     * @date 2023/6/1 9:33
     */
    public void preHandleExperimentScheme(String experimentInstanceId, String caseInstanceId, String experimentGroupId) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(experimentGroupId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        CaseSchemeEntity caseSchemeEntity = tenantCaseSchemeBiz.getByInstanceId(caseInstanceId);
        if (BeanUtil.isEmpty(caseSchemeEntity)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        ExperimentSchemeEntity entity = ExperimentSchemeEntity.builder()
                    .experimentSchemeId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .questionSectionId(questionSectionId)
                    .questionSectionResultId(null)
                    .experimentGroupId(experimentGroupId)
                    .build();
        experimentSchemeService.save(entity);
    }
}
