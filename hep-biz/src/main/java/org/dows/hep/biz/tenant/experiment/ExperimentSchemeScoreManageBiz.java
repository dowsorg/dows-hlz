package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.tenant.experiment.request.ExperimentSchemeScoreRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentSchemeScoreItemResponse;
import org.dows.hep.api.tenant.experiment.response.ExperimentSchemeScoreResponse;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptReviewStateEnum;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.entity.ExperimentSchemeScoreEntity;
import org.dows.hep.entity.ExperimentSchemeScoreItemEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSchemeScoreItemService;
import org.dows.hep.service.ExperimentSchemeScoreService;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentSchemeScoreManageBiz {
    private final ExperimentManageBaseBiz baseBiz;
    private final ExperimentSchemeService experimentSchemeService;
    private final TenantCaseSchemeBiz tenantCaseSchemeBiz;
    private final ExperimentSchemeScoreService experimentSchemeScoreService;
    private final ExperimentSchemeScoreItemService experimentSchemeScoreItemService;
    private final ExperimentParticipatorService experimentParticipatorService;

    /**
     * @param
     * @return
     * @author fhb
     * @description 必须在 `ExperimentSchemeManageBiz` 之后执行
     * @date 2023/6/15 20:36
     */
    public void preHandleExperimentSchemeScore(String experimentInstanceId, String caseInstanceId) {
        List<String> viewAccountIds = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, 0)
                .list()
                .stream()
                .map(ExperimentParticipatorEntity::getAccountId)
                .toList();
        preHandleExperimentSchemeScore(experimentInstanceId, caseInstanceId, viewAccountIds);
    }

    public void preHandleExperimentSchemeScore(String experimentInstanceId, String caseInstanceId, List<String> viewAccountIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(viewAccountIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 获取该案例 `caseInstanceId` 下方案设计
        CaseSchemeResponse caseScheme = tenantCaseSchemeBiz.getCaseSchemeByInstanceId(caseInstanceId);
        if (BeanUtil.isEmpty(caseScheme)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }

        // 获取该实验下 `experimentInstanceId` 所有的方案设计
        List<ExperimentSchemeEntity> schemeList = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .list();
        if (CollUtil.isEmpty(schemeList)) {
            return;
        }

        // 预生成评分表 `experimentSchemeScore`
        List<ExperimentSchemeScoreEntity> scoreList = new ArrayList<>();
        viewAccountIds.forEach(accountId -> {
            schemeList.forEach(scheme -> {
                ExperimentSchemeScoreEntity scoreEntity = ExperimentSchemeScoreEntity.builder()
                        .experimentSchemeScoreId(baseBiz.getIdStr())
                        .experimentSchemeId(scheme.getExperimentSchemeId())
                        .caseSchemeId(caseScheme.getCaseSchemeId())
                        .reviewAccountId(accountId)
                        .reviewScore(0.0f)
                        .reviewDt(null)
                        .reviewState(ExptReviewStateEnum.NOT_SUBMITTED.getCode())
                        .build();
                scoreList.add(scoreEntity);
            });
        });
        experimentSchemeScoreService.saveBatch(scoreList);

        // 获取该案例下 `caseInstanceId` 所有评分维度
        List<QuestionSectionDimensionResponse> dimensionList = caseScheme.getQuestionSectionDimensionList();
        if (CollUtil.isEmpty(dimensionList)) {
            return;
        }

        // 预生成评分详细表 `experimentSchemeScoreItem`
        List<ExperimentSchemeScoreItemEntity> itemList = new ArrayList<>();
        scoreList.forEach(score -> {
            dimensionList.forEach(dimension -> {
                ExperimentSchemeScoreItemEntity itemEntity = ExperimentSchemeScoreItemEntity.builder()
                        .experimentSchemeScoreItemId(baseBiz.getIdStr())
                        .experimentSchemeScoreId(score.getExperimentSchemeScoreId())
                        .dimensionName(dimension.getDimensionName())
                        .dimensionContent(dimension.getDimensionContent())
                        .minScore(dimension.getMinScore())
                        .maxScore(dimension.getMaxScore())
                        .score(0.0f)
                        .build();
                itemList.add(itemEntity);
            });
        });
        experimentSchemeScoreItemService.saveBatch(itemList);
    }

    /**
     * @author fhb
     * @description 获取方案设计评分表
     * @date 2023/6/15 21:35
     * @param
     * @return
     */
    public List<ExperimentSchemeScoreResponse> listSchemeScore(String experimentSchemeId, String reviewAccountId) {
        Assert.notNull(experimentSchemeId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(reviewAccountId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 是否是管理员
        boolean isAdmin = baseBiz.isAdministrator(reviewAccountId);
        // 管理员看到所有的评分表, 普通角色仅仅看到自己的评分表
        List<ExperimentSchemeScoreEntity> scoreList = experimentSchemeScoreService.lambdaQuery()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeId, experimentSchemeId)
                .eq(!isAdmin, ExperimentSchemeScoreEntity::getReviewAccountId, reviewAccountId)
                .list();
        if (CollUtil.isEmpty(scoreList)) {
            return new ArrayList<>();
        }

        List<String> schemeScoreIdList = scoreList.stream()
                .map(ExperimentSchemeScoreEntity::getExperimentSchemeScoreId)
                .toList();
        List<ExperimentSchemeScoreItemEntity> itemList = experimentSchemeScoreItemService.lambdaQuery()
                .in(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreId, schemeScoreIdList)
                .list();

        // convert
        List<ExperimentSchemeScoreResponse> result = BeanUtil.copyToList(scoreList, ExperimentSchemeScoreResponse.class);
        List<ExperimentSchemeScoreItemResponse> resultItemList = BeanUtil.copyToList(itemList, ExperimentSchemeScoreItemResponse.class);
        Map<String, List<ExperimentSchemeScoreItemResponse>> collect = resultItemList.stream()
                .collect(Collectors.groupingBy(ExperimentSchemeScoreItemResponse::getExperimentSchemeScoreId));
        result.forEach(r -> {
            List<ExperimentSchemeScoreItemResponse> rItemList = collect.get(r.getExperimentSchemeScoreId());
            Map<String, List<ExperimentSchemeScoreItemResponse>> rItemMap = rItemList.stream()
                    .collect(Collectors.groupingBy(ExperimentSchemeScoreItemResponse::getDimensionName));
            r.setItemList(rItemList);
            r.setItemMap(rItemMap);
        });

        return result;
    }

    /**
     * @author fhb
     * @description 提交方案设计评分表
     * @date 2023/6/15 21:52
     */
    public Boolean submitSchemeScore(ExperimentSchemeScoreRequest request, String submitAccountId) {
        Assert.notNull(request, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(request.getExperimentSchemeScoreId(), ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(request.getItemList(), ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(submitAccountId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        boolean isAdmin = baseBiz.isAdministrator(submitAccountId);
        ExperimentSchemeScoreEntity schemeScore = getSchemeScore(request.getExperimentSchemeScoreId());
        String reviewAccountId = schemeScore.getReviewAccountId();
        if (!isAdmin && Objects.equals(submitAccountId, reviewAccountId)) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }

        List<ExperimentSchemeScoreItemEntity> itemEntityList = new ArrayList<>();
        List<ExperimentSchemeScoreRequest.ExperimentSchemeScoreItemRequest> itemList = request.getItemList();
        List<String> itemIdList = itemList.stream()
                .map(ExperimentSchemeScoreRequest.ExperimentSchemeScoreItemRequest::getExperimentSchemeScoreItemId)
                .toList();
        Map<String, Long> idCollect = experimentSchemeScoreItemService.lambdaQuery()
                .in(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreItemId, itemIdList)
                .list()
                .stream()
                .collect(Collectors.toMap(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreItemId, ExperimentSchemeScoreItemEntity::getId));
        itemList.forEach(item -> {
            ExperimentSchemeScoreItemEntity itemEntity = ExperimentSchemeScoreItemEntity.builder()
                    .id(idCollect.get(item.getExperimentSchemeScoreItemId()))
                    .score(item.getScore())
                    .build();
            itemEntityList.add(itemEntity);
        });
        return experimentSchemeScoreItemService.updateBatchById(itemEntityList);
    }

    private ExperimentSchemeScoreEntity getSchemeScore(String experimentSchemeScoreId) {
        return experimentSchemeScoreService.lambdaQuery()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeScoreId, experimentSchemeScoreId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
    }
}
