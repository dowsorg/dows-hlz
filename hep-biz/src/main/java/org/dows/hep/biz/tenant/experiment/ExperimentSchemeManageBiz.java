package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.response.QuestionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.entity.ExperimentSchemeItemEntity;
import org.dows.hep.service.ExperimentSchemeItemService;
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
    private final ExperimentSchemeItemService experimentSchemeItemService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 预生成方案设计试卷-分配实验的时候调用
     * @date 2023/6/1 9:33
     */
    public void preHandleExperimentScheme(String experimentInstanceId, String caseInstanceId, List<String> experimentGroupIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(experimentGroupIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 案例下方案设计
        CaseSchemeResponse caseScheme = tenantCaseSchemeBiz.getCaseSchemeByInstanceId(caseInstanceId);
        if (BeanUtil.isEmpty(caseScheme)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }

        // 为每个小组分配试卷
        List<ExperimentSchemeEntity> entityList = new ArrayList<>();
        List<ExperimentSchemeItemEntity> itemEntityList = new ArrayList<>();
        experimentGroupIds.forEach(groupId -> {
            // experiment-scheme
            ExperimentSchemeEntity entity = ExperimentSchemeEntity.builder()
                    .experimentSchemeId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstanceId)
                    .experimentGroupId(groupId)
                    .schemeName(caseScheme.getSchemeName())
                    .schemeDescr(caseScheme.getSchemeDescr())
                    .containsVideo(caseScheme.getContainsVideo())
                    .videoQuestion(caseScheme.getVideoQuestion())
                    .state(0)
                    .build();
            entityList.add(entity);

            // experiment-scheme-item
            List<ExperimentSchemeItemEntity> localItemList = new ArrayList<>();
            List<QuestionSectionItemResponse> sectionItemList = caseScheme.getSectionItemList();
            if (CollUtil.isNotEmpty(sectionItemList)) {
                sectionItemList.forEach(sectionItem -> {
                    QuestionResponse question = sectionItem.getQuestion();
                    List<ExperimentSchemeItemEntity> itemEntities = convertToFlatList(question);
                    localItemList.addAll(itemEntities);
                });

                for (int i = 0; i < localItemList.size(); i++) {
                    ExperimentSchemeItemEntity item = localItemList.get(i);
                    item.setSeq(i);
                    item.setExperimentSchemeId(entity.getExperimentSchemeId());
                }

                itemEntityList.addAll(localItemList);
            }
        });

        experimentSchemeService.saveBatch(entityList);
        experimentSchemeItemService.saveBatch(itemEntityList);
    }

    private List<ExperimentSchemeItemEntity> convertToFlatList(QuestionResponse questionResponse) {
        List<ExperimentSchemeItemEntity> flatList = new ArrayList<>();
        flattenTree(questionResponse, flatList, "0");
        return flatList;
    }

    private void flattenTree(QuestionResponse questionResponse, List<ExperimentSchemeItemEntity> flatList, String pid) {
        // 处理当前节点
        ExperimentSchemeItemEntity itemEntity = ExperimentSchemeItemEntity.builder()
                .experimentSchemeItemId(idGenerator.nextIdStr())
                .experimentSchemeItemPid(pid)
                .experimentSchemeId(null)
                .questionTitle(questionResponse.getQuestionTitle())
                .questionDescr(questionResponse.getQuestionDescr())
                .questionDetailedAnswer(questionResponse.getDetailedAnswer())
                .accountId(null)
                .seq(null)
                .questionResult(null)
                .build();
        flatList.add(itemEntity);

        // 判空
        List<QuestionResponse> children = questionResponse.getChildren();
        if (CollUtil.isEmpty(children)) {
            return;
        }

        // 处理子节点
        for (QuestionResponse child : questionResponse.getChildren()) {
            flattenTree(child, flatList, itemEntity.getExperimentSchemeItemId());
        }
    }
}
