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

import java.util.ArrayList;
import java.util.List;

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
    public void preHandleExperimentScheme(String experimentInstanceId, String caseInstanceId, List<String> experimentGroupIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(experimentGroupIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 案例下方案设计
        CaseSchemeEntity caseSchemeEntity = tenantCaseSchemeBiz.getByInstanceId(caseInstanceId);
        if (BeanUtil.isEmpty(caseSchemeEntity)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }

        // 为每个小组分配试卷
        List<ExperimentSchemeEntity> entityList = new ArrayList<>();
        String caseSchemeId = caseSchemeEntity.getCaseSchemeId();
        experimentGroupIds.forEach(groupId -> {
            ExperimentSchemeEntity entity = ExperimentSchemeEntity.builder()
                    .experimentSchemeId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .caseSchemeId(caseSchemeId)
                    .questionSectionResultId(null)
                    .experimentGroupId(groupId)
                    .build();
            entityList.add(entity);
        });

        experimentSchemeService.saveBatch(entityList);
    }
}
