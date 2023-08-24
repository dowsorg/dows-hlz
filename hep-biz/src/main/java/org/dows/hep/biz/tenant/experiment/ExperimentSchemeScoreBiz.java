package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.api.tenant.experiment.request.ExperimentSchemeScoreRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.tenant.experiment.response.ExperimentSchemeScoreItemResponse;
import org.dows.hep.api.tenant.experiment.response.ExperimentSchemeScoreResponse;
import org.dows.hep.api.tenant.experiment.response.ExptSchemeGroupReviewResponse;
import org.dows.hep.api.tenant.experiment.vo.ExptSchemeScoreReviewVO;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptReviewStateEnum;
import org.dows.hep.api.user.experiment.ExptSchemeStateEnum;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExperimentSchemeScoreBiz {
    private final ExperimentManageBaseBiz baseBiz;
    private final TenantCaseSchemeBiz tenantCaseSchemeBiz;
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentSchemeService experimentSchemeService;
    private final ExperimentSchemeScoreService experimentSchemeScoreService;
    private final ExperimentSchemeScoreItemService experimentSchemeScoreItemService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentGroupService experimentGroupService;

    private static final String ADMIN_ACCOUNT_ID = "1001010086";

    /**
     * @param experimentInstanceId - 实验实例ID
     * @param caseInstanceId       - 案例实例ID
     * @author fhb
     * @description 必须在 `ExperimentSchemeManageBiz` 之后执行
     * @date 2023/6/15 20:36
     */
    public void preHandleExperimentSchemeScore(String experimentInstanceId, String caseInstanceId) {
        List<String> viewAccountIds = new ArrayList<>();
        // 管理员不参与评分了
//        viewAccountIds.add(ADMIN_ACCOUNT_ID);
        List<String> tAccountIds = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, 0)
                .list()
                .stream()
                .map(ExperimentParticipatorEntity::getAccountId)
                .toList();
        viewAccountIds.addAll(tAccountIds);
        preHandleExperimentSchemeScore(experimentInstanceId, caseInstanceId, viewAccountIds);
    }

    /**
     * @param exptInstanceId  - 实验实例ID
     * @param reviewAccountId - 评审账号ID
     * @return java.util.List<org.dows.hep.api.tenant.experiment.response.ExptSchemeGroupReviewResponse>
     * @author fhb
     * @description 获取方案设计报告的小组列表
     * @date 2023/7/12 16:37
     */
    public List<ExptSchemeGroupReviewResponse> listSchemeGroupReview(String exptInstanceId, String reviewAccountId) {
        // 获取实验下 `exptInstanceId` 小组信息
        List<ExperimentGroupResponse> exptGroupList = baseBiz.listExptGroup(exptInstanceId);
        if (CollUtil.isEmpty(exptGroupList)) {
            throw new BizException("获取方案设计报告的小组列表时，小组信息为空");
        }

        // 获取该实验下 `exptInstanceId` 方案设计
        List<ExperimentSchemeEntity> schemeList = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, exptInstanceId)
                .list();
        if (CollUtil.isEmpty(schemeList)) {
            throw new BizException("获取方案设计报告的小组列表时，方案设计数据为空");
        }

        // 获取 `exptInstanceId` && `reviewAccountId` 下所有的方案设计评分
        // 是否是管理员
        boolean isAdmin = baseBiz.isAdministrator(reviewAccountId);
        // schemeId list
        List<String> schemeIdList = schemeList.stream()
                .map(ExperimentSchemeEntity::getExperimentSchemeId)
                .toList();
        List<ExperimentSchemeScoreEntity> schemeScoreList = experimentSchemeScoreService.lambdaQuery()
                .in(ExperimentSchemeScoreEntity::getExperimentSchemeId, schemeIdList)
                .eq(!isAdmin, ExperimentSchemeScoreEntity::getReviewAccountId, reviewAccountId)
                .list();

        // group-id map expt-scheme
        Map<String, ExperimentSchemeEntity> groupIdMapExptScheme = schemeList.stream()
                .collect(Collectors.toMap(ExperimentSchemeEntity::getExperimentGroupId, item -> item));
        // scheme-id map scheme-score
        Map<String, ExperimentSchemeScoreEntity> schemeIdMapSchemeScore = schemeScoreList.stream()
                .collect(Collectors.toMap(ExperimentSchemeScoreEntity::getExperimentSchemeId, item -> item, (v1, v2) -> v1));

        // build result
        List<ExptSchemeGroupReviewResponse> result = new ArrayList<>();
        exptGroupList.forEach(group -> {
            ExperimentSchemeEntity schemeEntity = groupIdMapExptScheme.get(group.getExperimentGroupId());
            Assert.notNull(schemeEntity, "获取方案设计报告的小组列表时: 小组的方案设计数据为空");
            ExperimentSchemeScoreEntity schemeScoreEntity = schemeIdMapSchemeScore.get(schemeEntity.getExperimentSchemeId());
            Assert.notNull(schemeScoreEntity, "获取方案设计报告的小组列表时: 方案设计评分表数据为空");
            ExptSchemeGroupReviewResponse resultItem = ExptSchemeGroupReviewResponse.builder()
                    .exptGroupId(group.getExperimentGroupId())
                    .exptGroupName(group.getGroupName())
                    .exptGroupAliasName(group.getGroupAlias())
                    .groupNo(group.getGroupNo())
                    .exptSchemeStateCode(isAdmin ? schemeEntity.getState() : schemeScoreEntity.getReviewState())
                    .exptSchemeStateName(isAdmin ? ExptSchemeStateEnum.getByCode(schemeEntity.getState()).getName() : ExptReviewStateEnum.getByCode(schemeScoreEntity.getReviewState()).getName())
                    .reviewDt(schemeScoreEntity.getReviewDt())
                    .reviewScore(isAdmin ? (schemeEntity.getScore() == null ? 0 : schemeEntity.getScore()) : (schemeScoreEntity.getReviewScore() == null ? 0 : schemeScoreEntity.getReviewScore()))
                    .build();
            result.add(resultItem);
        });

        return result;
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param accountId      - 账号
     * @return boolean
     * @author fhb
     * @description 判断是否有评分操作权限
     * @date 2023/8/1 13:13
     */
    public boolean canReview(String exptInstanceId, String accountId) {
        boolean isAdmin = baseBiz.isAdministrator(accountId);
        ExperimentSetting.SchemeSetting schemeSetting = experimentSettingBiz.getSchemeSetting(exptInstanceId);
        Assert.notNull(schemeSetting, "方案设计评分权限校验：获取实验设置信息异常");

        Date schemeEndTime = schemeSetting.getSchemeEndTime();
        Assert.notNull(schemeEndTime, "方案设计评分权限校验：获取方案设计作答截止时间信息异常");

        Date scoreEndTime = schemeSetting.getScoreEndTime();
        Assert.notNull(scoreEndTime, "提交方案设计评分表时：获取方案设计评分截止时间信息异常");

        Date auditEndTime = schemeSetting.getAuditEndTime();
        Assert.notNull(scoreEndTime, "提交方案设计评分表时：获取方案设计审核截止时间信息异常");

        Date currentDate = new Date();
        if (isAdmin) {
            return DateUtil.compare(currentDate, scoreEndTime) > 0 && DateUtil.compare(currentDate, auditEndTime) < 0;
        } else {
            return DateUtil.compare(currentDate, schemeEndTime) > 0 && DateUtil.compare(currentDate, scoreEndTime) < 0;
        }
    }

    /**
     * @param exptInstanceId  - 实验实例ID
     * @param reviewAccountId - 评审账号ID
     * @param exptGroupId     - 实验组ID
     * @return org.dows.hep.api.tenant.experiment.vo.ExptSchemeScoreReviewVO
     * @author fhb
     * @description 获取方案设计评分详情
     * @date 2023/7/12 19:51
     */
    public ExptSchemeScoreReviewVO getSchemeScoreReview(String exptInstanceId, String reviewAccountId, String exptGroupId) {
        // 方案设计-记录信息
        ExperimentGroupEntity exptGroup = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentGroupId, exptGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException("获取方案设计评分详情时：小组信息为空"));
        ExperimentSchemeResponse schemeInfo = experimentSchemeBiz.getScheme(exptInstanceId, exptGroupId, null, false);
        ExptSchemeScoreReviewVO.SchemeRecordInfo schemeRecordInfo = ExptSchemeScoreReviewVO.SchemeRecordInfo.builder()
                .groupName(exptGroup.getGroupName())
                .groupAlias(exptGroup.getGroupAlias())
                .schemeInfo(schemeInfo)
                .build();

        // 方案设计-评分信息
        List<ExperimentSchemeScoreResponse> scoreInfos = listSchemeScore(exptInstanceId, reviewAccountId, exptGroupId);
        ExptSchemeScoreReviewVO.SchemeScoreInfo schemeScoreInfo = ExptSchemeScoreReviewVO.SchemeScoreInfo.builder()
                .finalScore(schemeInfo == null ? 0 : (schemeInfo.getScore() == null ? 0 : schemeInfo.getScore()))
                .scoreInfos(scoreInfos)
                .build();

        return ExptSchemeScoreReviewVO.builder()
                .schemeRecordInfo(schemeRecordInfo)
                .schemeScoreInfo(schemeScoreInfo)
                .build();
    }

    /**
     * 如果是管理员，则可能是多个评分表一同提交
     * 如果不是管理员，则是单个评分表提交
     *
     * @author fhb
     * @description 提交方案设计评分表
     * @date 2023/6/15 21:52
     */
    @DSTransactional
    public String submitSchemeScore(ExperimentSchemeScoreRequest request, String submitAccountId) {
        /* 处理请求数据 */
        // check params and auth
        String experimentSchemeId = checkParamsAndAuth(request, submitAccountId);
        List<ExperimentSchemeScoreRequest.SchemeScoreRequest> scoreInfos = request.getScoreInfos();
        // 平铺所有请求中的 scoreIdList
        List<String> scoreIdList = scoreInfos.stream()
                .map(ExperimentSchemeScoreRequest.SchemeScoreRequest::getExperimentSchemeScoreId)
                .toList();
        // 平铺出所有请求中的 scoreItemList
        List<ExperimentSchemeScoreRequest.SchemeScoreItemRequest> itemList = scoreInfos.stream()
                .flatMap(item -> item.getItemList().stream())
                .toList();


        /* 准备数据 */
        // 获取对应 scoreIdList 的所有 oriEntityList
        List<ExperimentSchemeScoreEntity> oriEntityList = experimentSchemeScoreService.lambdaQuery()
                .in(ExperimentSchemeScoreEntity::getExperimentSchemeScoreId, scoreIdList)
                .list();
        Map<String, ExperimentSchemeScoreEntity> schemeIdMapEntity = oriEntityList.stream()
                .collect(Collectors.toMap(ExperimentSchemeScoreEntity::getExperimentSchemeScoreId, item -> item));
        // 获取对应 scoreItemList 的所有 oriItemEntityList
        List<String> scoreItemIdList = itemList.stream()
                .map(ExperimentSchemeScoreRequest.SchemeScoreItemRequest::getExperimentSchemeScoreItemId)
                .toList();
        List<ExperimentSchemeScoreItemEntity> oriItemEntityList = experimentSchemeScoreItemService.lambdaQuery()
                .in(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreItemId, scoreItemIdList)
                .list();
        Map<String, ExperimentSchemeScoreItemEntity> scoreItemIdMapEntity = oriItemEntityList.stream()
                .collect(Collectors.toMap(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreItemId, item -> item));
        // 获取 scheme-score 对应的 expt-scheme
        List<String> schemeIdList = oriEntityList.stream()
                .map(ExperimentSchemeScoreEntity::getExperimentSchemeId)
                .toList();
        // 获取评分表取值范围
        float maxScore = 0.00f;
        String firstSchemeScoreId = scoreIdList.get(0);
        List<ExperimentSchemeScoreItemEntity> firstSchemeScoreItemList = experimentSchemeScoreItemService.lambdaQuery()
                .eq(ExperimentSchemeScoreItemEntity::getExperimentSchemeScoreId, firstSchemeScoreId)
                .list();
        if (CollUtil.isEmpty(firstSchemeScoreItemList)) {
            throw new BizException("提交方案设计评分表时, 获取评分表条目异常");
        }
        Map<String, List<ExperimentSchemeScoreItemEntity>> collect = firstSchemeScoreItemList.stream()
                .collect(Collectors.groupingBy(ExperimentSchemeScoreItemEntity::getDimensionName));
        for (Map.Entry<String, List<ExperimentSchemeScoreItemEntity>> entry : collect.entrySet()) {
            List<ExperimentSchemeScoreItemEntity> contentList = entry.getValue();
            contentList.sort((v1, v2) -> (int) (v2.getMaxScore() - v1.getMaxScore()));
            ExperimentSchemeScoreItemEntity itemEntity = contentList.get(0);
            Float itemMaxScore = itemEntity.getMaxScore();
            maxScore += itemMaxScore;
        }


        /* 处理数据 */
        // check item score range
        checkItemScoreRange(itemList, oriItemEntityList);

        // 批量更新 SchemeScoreItem 的得分
        boolean updScoreItemRes = updSchemeScoreItemScore(itemList, scoreItemIdMapEntity);
        // 更新 SchemeScore 的得分和状态
        boolean updScoreRes = updSchemeScoreAndState(scoreInfos, schemeIdMapEntity);

        // 更新 exptScheme 的得分和状态
        BigDecimal score = calFinalScore(experimentSchemeId, maxScore);
        boolean updSchemeRes = updSchemeState(score, schemeIdList.get(0));

        return score.toString();
    }

    private void preHandleExperimentSchemeScore(String experimentInstanceId, String caseInstanceId, List<String> viewAccountIds) {
        Assert.notNull(experimentInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notNull(caseInstanceId, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        Assert.notEmpty(viewAccountIds, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());

        // 获取该案例 `caseInstanceId` 下方案设计
        CaseSchemeResponse caseScheme = tenantCaseSchemeBiz.getCaseSchemeByInstanceId(caseInstanceId);
        if (BeanUtil.isEmpty(caseScheme)) {
            throw new BizException(ExperimentESCEnum.SCHEME_NOT_NULL);
        }

        // 获取该案例下 `caseInstanceId` 所有评分维度
        List<QuestionSectionDimensionResponse> dimensionList = caseScheme.getQuestionSectionDimensionList();
        if (CollUtil.isEmpty(dimensionList)) {
            return;
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

    private List<ExperimentSchemeScoreResponse> listSchemeScore(String exptInstanceId, String reviewAccountId, String exptGroupId) {
        Assert.notNull(exptInstanceId, "获取方案设计评分详情时：实验实例ID不能为空");
        Assert.notNull(reviewAccountId, "获取方案设计评分详情时：实验评审人账号ID不能为空");
        Assert.notNull(exptGroupId, "获取方案设计评分详情时：实验小组ID不能为空");

        // 获取 `exptInstanceId` && `exptGroupId` 的方案设计
        ExperimentSchemeEntity exptScheme = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentSchemeEntity::getExperimentGroupId, exptGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException("获取方案设计评分详情时：获取小组方案设计数据异常"));

        // 是否是管理员
        boolean isAdmin = baseBiz.isAdministrator(reviewAccountId);
        // 管理员看到所有的评分表, 普通角色仅仅看到自己的评分表
        List<ExperimentSchemeScoreEntity> scoreList = experimentSchemeScoreService.lambdaQuery()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeId, exptScheme.getExperimentSchemeId())
                .eq(!isAdmin, ExperimentSchemeScoreEntity::getReviewAccountId, reviewAccountId)
                .list();
        if (CollUtil.isEmpty(scoreList)) {
            return new ArrayList<>();
        }

        Map<String, String> teacherNameMap = new HashMap<>();
        if (isAdmin) {
            scoreList.forEach(scoreEntity -> {
                String tempAccountId = scoreEntity.getReviewAccountId();
                // todo @UIM 提供批量优化方法
                String userName = baseBiz.getUserName(tempAccountId, "获取方案设计评分详情时，获取教师账号信息异常");
                teacherNameMap.put(tempAccountId, userName);
            });
            // 兼容旧数据
            teacherNameMap.put(ADMIN_ACCOUNT_ID, "管理员");
        }

        // list item
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
            // 设置教师名
            String accountId = r.getReviewAccountId();
            r.setReviewAccountName(teacherNameMap.get(accountId));

            // 设置评分表
            List<ExperimentSchemeScoreItemResponse> rItemList = collect.get(r.getExperimentSchemeScoreId());
            Map<String, List<ExperimentSchemeScoreItemResponse>> rItemMap = rItemList.stream()
                    .collect(Collectors.groupingBy(ExperimentSchemeScoreItemResponse::getDimensionName));
            r.setItemList(rItemList);
            r.setItemMap(rItemMap);
        });

        return result;
    }

    private List<ExperimentSchemeScoreEntity> listSchemeScore(String exptSchemeId) {
        return experimentSchemeScoreService.lambdaQuery()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeId, exptSchemeId)
                .list();
    }

    private ExperimentSchemeScoreEntity getSchemeScore(String experimentSchemeScoreId) {
        return experimentSchemeScoreService.lambdaQuery()
                .eq(ExperimentSchemeScoreEntity::getExperimentSchemeScoreId, experimentSchemeScoreId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
    }

    private String checkParamsAndAuth(ExperimentSchemeScoreRequest request, String submitAccountId) {
        Assert.notNull(request, "提交方案设计评分表时：请求参数不能为空");
        Assert.notNull(request.getScoreInfos(), "提交方案设计评分表时：请求参数不能为空");
        Assert.notNull(submitAccountId, "提交方案设计评分表时：评审人账号ID不能为空");

        // get scoreEndTime and auditEndTime
        List<ExperimentSchemeScoreRequest.SchemeScoreRequest> scoreInfos = request.getScoreInfos();
        ExperimentSchemeScoreRequest.SchemeScoreRequest first = scoreInfos.get(0);
        ExperimentSchemeScoreEntity firstSchemeScoreEntity = getSchemeScore(first.getExperimentSchemeScoreId());
        String experimentSchemeId = firstSchemeScoreEntity.getExperimentSchemeId();
        String exptInstanceId = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .oneOpt()
                .map(ExperimentSchemeEntity::getExperimentInstanceId)
                .orElseThrow(() -> new BizException("提交方案设计评分表时：获取实验实例异常"));
        ExperimentSetting.SchemeSetting schemeSetting = experimentSettingBiz.getSchemeSetting(exptInstanceId);
        Assert.notNull(schemeSetting, "提交方案设计评分表时：获取实验设置信息异常");
        Date scoreEndTime = schemeSetting.getScoreEndTime();
        Assert.notNull(scoreEndTime, "提交方案设计评分表时：获取方案设计评分截止时间信息异常");
        Date auditEndTime = schemeSetting.getAuditEndTime();
        if (auditEndTime == null) {
            auditEndTime = scoreEndTime;
        }

        // check
        Date currentDate = new Date();
        boolean isAdmin = baseBiz.isAdministrator(submitAccountId);
        if (!isAdmin) {
            // check auth
            String reviewAccountId = firstSchemeScoreEntity.getReviewAccountId();
            if (!Objects.equals(submitAccountId, reviewAccountId)) {
                throw new BizException("提交方案设计评分表时：评审人账号没有该评分表的操作权限");
            }

            // check date
            if (DateUtil.compare(currentDate, scoreEndTime) > 0) {
                throw new BizException("提交方案设计评分表时：已过评审时间，请联系管理员");
            }
        } else {
            // check date
            if (DateUtil.compare(currentDate, scoreEndTime) < 0) {
                throw new BizException("提交方案设计评分表时：评分还未结束，暂不可以修改评分");
            }
        }
        if (DateUtil.compare(currentDate, auditEndTime) > 0) {
            throw new BizException("提交方案设计评分表时：审核已截止");
        }

        return experimentSchemeId;
    }

    private void checkItemScoreRange(List<ExperimentSchemeScoreRequest.SchemeScoreItemRequest> itemList, List<ExperimentSchemeScoreItemEntity> oriItemEntityList) {
        Map<String, List<ExperimentSchemeScoreItemEntity>> groupByName = oriItemEntityList.stream()
                .collect(Collectors.groupingBy(ExperimentSchemeScoreItemEntity::getDimensionName));
        Map<String, Float> idMapMaxValue = new HashMap<>();
        Map<String, Float> idMapMinValue = new HashMap<>();
        groupByName.forEach((k, v) -> {
            Float max = v.stream().max((v1, v2) -> (int) (v1.getMaxScore() - v2.getMaxScore()))
                    .map(ExperimentSchemeScoreItemEntity::getMaxScore)
                    .orElseThrow(() -> new BizException("提交方案设计评分表时：获取评分最大值异常"));
            Float min = v.stream().min((v1, v2) -> (int) (v1.getMaxScore() - v2.getMaxScore()))
                    .map(ExperimentSchemeScoreItemEntity::getMinScore)
                    .orElseThrow(() -> new BizException("提交方案设计评分表时：获取评分最小值异常"));
            v.forEach(item -> {
                String experimentSchemeScoreItemId = item.getExperimentSchemeScoreItemId();
                idMapMaxValue.put(experimentSchemeScoreItemId, max);
                idMapMinValue.put(experimentSchemeScoreItemId, min);
            });
        });
        itemList.forEach(item -> {
            String experimentSchemeScoreItemId = item.getExperimentSchemeScoreItemId();
            Float score = item.getScore();

            Float minScore = idMapMinValue.get(experimentSchemeScoreItemId);
            Float maxScore = idMapMaxValue.get(experimentSchemeScoreItemId);
            if (score < minScore) {
                throw new BizException("提交方案设计评分表时：分数不能小于该组最小值");
            }
            if (score > maxScore) {
                throw new BizException("提交方案设计评分表时：分数不能大于该组最大值");
            }
        });
    }

    private boolean updSchemeScoreItemScore(List<ExperimentSchemeScoreRequest.SchemeScoreItemRequest> itemList, Map<String, ExperimentSchemeScoreItemEntity> scoreItemIdMapEntity) {
        List<ExperimentSchemeScoreItemEntity> itemEntityList = new ArrayList<>();
        itemList.forEach(item -> {
            ExperimentSchemeScoreItemEntity oriItemEntity = scoreItemIdMapEntity.get(item.getExperimentSchemeScoreItemId());
            ExperimentSchemeScoreItemEntity itemEntity = ExperimentSchemeScoreItemEntity.builder()
                    .id(oriItemEntity.getId())
                    .score(item.getScore())
                    .build();
            itemEntityList.add(itemEntity);
        });
        return experimentSchemeScoreItemService.updateBatchById(itemEntityList);
    }

    private boolean updSchemeScoreAndState(List<ExperimentSchemeScoreRequest.SchemeScoreRequest> scoreInfos, Map<String, ExperimentSchemeScoreEntity> schemeIdMapEntity) {
        List<ExperimentSchemeScoreEntity> entityList = new ArrayList<>();
        scoreInfos.forEach(scoreInfo -> {
            String experimentSchemeScoreId = scoreInfo.getExperimentSchemeScoreId();
            Float reviewScore = scoreInfo.getReviewScore() == null ? 0.00f : scoreInfo.getReviewScore();

            ExperimentSchemeScoreEntity experimentSchemeScoreEntity = schemeIdMapEntity.get(experimentSchemeScoreId);
            ExperimentSchemeScoreEntity entity = ExperimentSchemeScoreEntity.builder()
                    .id(experimentSchemeScoreEntity.getId())
                    .reviewScore(reviewScore)
                    .reviewState(ExptReviewStateEnum.REVIEWED.getCode())
                    .reviewDt(new Date())
                    .build();
            entityList.add(entity);
        });
        return experimentSchemeScoreService.updateBatchById(entityList);
    }

    private BigDecimal calFinalScore(String exptSchemeId, float maxScore) {
        if (StrUtil.isBlank(exptSchemeId)) {
            return BigDecimal.ZERO;
        }

        BigDecimal finalScore = BigDecimal.ZERO;
        BigDecimal totalScore = BigDecimal.ZERO;
        // 将评分转换为以100为基数的
        List<ExperimentSchemeScoreEntity> scoreInfos = listSchemeScore(exptSchemeId);
        for (ExperimentSchemeScoreEntity scoreInfo : scoreInfos) {
            BigDecimal scoreBy100;
            float reviewScore = scoreInfo.getReviewScore() == null ? 0.0f : scoreInfo.getReviewScore();
            if ((int) reviewScore == (int) maxScore) {
                scoreBy100 = new BigDecimal("100");
            } else {
                BigDecimal bMaxScore = BigDecimal.valueOf(maxScore);
                BigDecimal bReviewScore = BigDecimal.valueOf(reviewScore);
                scoreBy100 = bReviewScore.divide(bMaxScore, 2, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }
            totalScore = totalScore.add(scoreBy100);
        }
        // 计算平均值
        finalScore = totalScore.divide(BigDecimal.valueOf(scoreInfos.size()), 0, RoundingMode.DOWN);
        return finalScore;
    }

    private boolean updSchemeState(BigDecimal score, String schemeId) {
        // 有任何一个未审批状态则为未审批
        Integer state = ExptSchemeStateEnum.SUBMITTED.getCode();
        List<ExperimentSchemeScoreEntity> schemeScoreEntityList = listSchemeScore(schemeId);
        ExperimentSchemeScoreEntity experimentSchemeScoreEntity = schemeScoreEntityList.stream()
                .filter(item -> Objects.equals(item.getReviewState(), ExptReviewStateEnum.UNREVIEWED.getCode()))
                .findAny()
                .orElse(null);
        if (experimentSchemeScoreEntity == null) {
            state = ExptSchemeStateEnum.SCORED.getCode();
        }
        return experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, schemeId)
                .set(ExperimentSchemeEntity::getState, state)
                .set(ExperimentSchemeEntity::getScore, score)
                .update();
    }
}
