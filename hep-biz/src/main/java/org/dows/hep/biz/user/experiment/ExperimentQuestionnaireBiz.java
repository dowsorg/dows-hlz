package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.ExptQuestionnaireStateEnum;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentQuestionnaireRequest;
import org.dows.hep.api.user.experiment.request.ExptQuestionnaireAllotRequest;
import org.dows.hep.api.user.experiment.request.ExptQuestionnaireSearchRequest;
import org.dows.hep.api.user.experiment.response.ExperimentPeriodsResonse;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.entity.ExperimentQuestionnaireEntity;
import org.dows.hep.service.ExperimentQuestionnaireService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/6/3 20:43
 */
@RequiredArgsConstructor
@Service
public class ExperimentQuestionnaireBiz {
    private final ExperimentQuestionnaireService experimentQuestionnaireService;
    private final ExperimentQuestionnaireItemBiz experimentQuestionnaireItemBiz;
    private final ExperimentTimerBiz experimentTimerBiz;
    private final ExperimentQuestionnaireScoreBiz experimentQuestionnaireScoreBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description 为试卷设置答题者-在实验分配机构完成后通过事件调用
     * @date 2023/6/3 20:53
     */
    public void allotQuestionnaireMembers(ExptQuestionnaireAllotRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // request-param
        String experimentInstanceId = request.getExperimentInstanceId();
        String experimentGroupId = request.getExperimentGroupId();
        List<ExptQuestionnaireAllotRequest.ParticipatorWithQuestionnaire> allotList = request.getAllotList();
        if (StrUtil.isBlank(experimentInstanceId) || StrUtil.isBlank(experimentGroupId) || CollUtil.isEmpty(allotList)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // orgId map accountId
        HashMap<String, String> collect = new HashMap<>();
        allotList.forEach(pq -> {
            String accountId = pq.getAccountId();
            List<String> experimentOrgIds = pq.getExperimentOrgIds();
            if (CollUtil.isNotEmpty(experimentOrgIds)) {
                experimentOrgIds.forEach(orgId -> {
                    collect.put(orgId, accountId);
                });
            }
        });

        // 当前实验、当前小组的所有试卷
        List<ExperimentQuestionnaireEntity> questionnaireList = listQuestionnaire(experimentInstanceId, experimentGroupId);
        if (CollUtil.isEmpty(questionnaireList)) {
            return;
        }

        // 为每份试卷分配 account
        questionnaireList.forEach(questionnaire -> {
            String orgId = questionnaire.getExperimentOrgId();
            questionnaire.setExperimentAccountId(collect.get(orgId));
        });

        experimentQuestionnaireService.updateBatchById(questionnaireList);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/6/3 20:53
     */
    public ExperimentQuestionnaireResponse getQuestionnaire(ExptQuestionnaireSearchRequest request) {
        Assert.notNull(request, ExperimentESCEnum.PARAMS_NON_NULL.getDescr());
        ExperimentPeriodsResonse experimentPeriods = experimentTimerBiz.getExperimentCurrentPeriods("3", request.getExperimentInstanceId());
        Integer currentPeriod = Optional.ofNullable(experimentPeriods)
                .map(ExperimentPeriodsResonse::getCurrentPeriod)
                .orElseThrow(() -> new BizException(ExperimentESCEnum.PERIOD_NON_NULL));

        // 根据实验id、期数、小组id， 机构id 获取知识答题
        ExperimentQuestionnaireEntity entity = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                .eq(ExperimentQuestionnaireEntity::getPeriodSequence, currentPeriod)
                .eq(ExperimentQuestionnaireEntity::getExperimentOrgId, request.getExperimentOrgId())
                .eq(ExperimentQuestionnaireEntity::getExperimentGroupId, request.getExperimentGroupId())
                .eq(ExperimentQuestionnaireEntity::getExperimentAccountId,  request.getExperimentAccountId())
                .oneOpt()
                .orElse(null);
        if (BeanUtil.isEmpty(entity)) {
            return new ExperimentQuestionnaireResponse();
        }
        ExperimentQuestionnaireResponse result = BeanUtil.copyProperties(entity, ExperimentQuestionnaireResponse.class);

        List<ExperimentQuestionnaireItemResponse> itemList = experimentQuestionnaireItemBiz.listByQuestionnaireId(entity.getExperimentQuestionnaireId());
        List<ExperimentQuestionnaireItemResponse> itemTreeList = convertList2Tree(itemList);
        List<ExperimentQuestionnaireResponse.ExptCategQuestionnaireItem> categItemList = ExperimentQuestionnaireResponse.convertItemList2CategItemList(itemTreeList);
        result.setItemList(itemTreeList);
        result.setCategItemList(categItemList);

        return result;
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId - 实验小组ID
     * @param needRightValue - 是否需要返回正确答案
     * @return org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse
     * @author fhb
     * @description 如果有小组ID， 则列出小组的知识答题信息，否则列出实验所有的知识答题信息
     * @date 2023/7/18 10:50
     */
    public List<ExperimentQuestionnaireResponse> listExptQuestionnaire(String exptInstanceId, String exptGroupId, boolean needRightValue) {
        Assert.notBlank(exptInstanceId, "根据实验ID获取知识答题数据时：请求参数实验ID不能为空");

        // 实验知识答题列表
        List<ExperimentQuestionnaireEntity> exptQuestionnaireList = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentQuestionnaireEntity::getExperimentGroupId, exptGroupId)
                .list();
        if (CollUtil.isEmpty(exptQuestionnaireList)) {
            return new ArrayList<>();
        }
        List<ExperimentQuestionnaireResponse> result = BeanUtil.copyToList(exptQuestionnaireList, ExperimentQuestionnaireResponse.class);

        // 实验知识答题item列表
        List<String> questionnaireIdList = exptQuestionnaireList.stream()
                .map(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId)
                .toList();
        List<ExperimentQuestionnaireItemResponse> itemList = experimentQuestionnaireItemBiz.listByQuestionnaireIds(questionnaireIdList, needRightValue);
        if (CollUtil.isEmpty(itemList)) {
            return result;
        }

        // build response
        Map<String, List<ExperimentQuestionnaireItemResponse>> idMapItemList = itemList.stream()
                .collect(Collectors.groupingBy(ExperimentQuestionnaireItemResponse::getExperimentQuestionnaireId));
        result.forEach(resultItem -> {
            List<ExperimentQuestionnaireItemResponse> resultItemItemList = idMapItemList.get(resultItem.getExperimentQuestionnaireId());
            List<ExperimentQuestionnaireItemResponse> itemTreeList = convertList2Tree(resultItemItemList);
            List<ExperimentQuestionnaireResponse.ExptCategQuestionnaireItem> categItemList = ExperimentQuestionnaireResponse.convertItemList2CategItemList(itemTreeList);
            resultItem.setItemList(itemTreeList);
            resultItem.setCategItemList(categItemList);
            result.add(resultItem);
        });

        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     */
    public Boolean updateQuestionnaire(ExperimentQuestionnaireRequest request, String updateAccountId) {
        if (BeanUtil.isEmpty(request) || StrUtil.isBlank(updateAccountId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // check
        cannotOperateAfterSubmit(request.getExperimentQuestionnaireId(), updateAccountId);

        // update
        List<ExperimentQuestionnaireItemRequest> itemList = request.getItemList();
        return experimentQuestionnaireItemBiz.updateBatch(itemList);
    }

//    /**
//     * @param
//     * @return
//     * @说明: 提交
//     * @关联表:
//     * @工时: 2H
//     * @开发者: lait
//     * @开始时间:
//     * @创建时间: 2023年4月23日 上午9:44:34
//     */
//    @DSTransactional
//    public Boolean submitQuestionnaire(String experimentQuestionnaireId, String accountId) {
//        if (StrUtil.isBlank(experimentQuestionnaireId) || StrUtil.isBlank(accountId)) {
//            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
//        }
//
//        // check
//        cannotOperateAfterSubmit(experimentQuestionnaireId, accountId);
//
//        // submit
//        boolean updateRes = experimentQuestionnaireService.lambdaUpdate()
//                .eq(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, experimentQuestionnaireId)
//                .set(ExperimentQuestionnaireEntity::getState, ExptQuestionnaireStateEnum.SUBMITTED.getCode())
//                .update();
//
//        // todo compute
//
//        return updateRes;
//    }

    /**
     * @param
     * @return
     * @说明: 批量提交
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean submitQuestionnaireBatch(String experimentInstanceId, Integer period) {
        if (StrUtil.isBlank(experimentInstanceId) || period == null) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        // submit
        boolean updateRes = experimentQuestionnaireService.lambdaUpdate()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentQuestionnaireEntity::getPeriodSequence, period)
                .set(ExperimentQuestionnaireEntity::getState, ExptQuestionnaireStateEnum.SUBMITTED.getCode())
                .update();

        // compute
//        experimentQuestionnaireScoreBiz.calculateExptQuestionnaireScore(experimentInstanceId, period);

        return updateRes;
    }

    private void cannotOperateAfterSubmit(String experimentQuestionnaireId, String submitAccountId) {
        ExperimentQuestionnaireEntity questionnaireEntity = experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentQuestionnaireId, experimentQuestionnaireId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));

        // check auth
        String experimentAccountId = questionnaireEntity.getExperimentAccountId();
        if (!Objects.equals(experimentAccountId, submitAccountId)) {
            throw new BizException(ExperimentESCEnum.NO_AUTHORITY);
        }

        // check state
        Integer state = questionnaireEntity.getState();
        if (Objects.equals(ExptQuestionnaireStateEnum.SUBMITTED.getCode(), state)) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
    }

    private List<ExperimentQuestionnaireEntity> listQuestionnaire(String experimentInstanceId, String experimentGroupId) {
        return experimentQuestionnaireService.lambdaQuery()
                .eq(ExperimentQuestionnaireEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentQuestionnaireEntity::getExperimentGroupId, experimentGroupId)
                .list();
    }

    private List<ExperimentQuestionnaireItemResponse> convertList2Tree(List<ExperimentQuestionnaireItemResponse> nodes) {
        Map<String, ExperimentQuestionnaireItemResponse> nodeMap = new HashMap<>();

        // 构建节点映射，方便根据id查找节点
        for (ExperimentQuestionnaireItemResponse node : nodes) {
            nodeMap.put(node.getExperimentQuestionnaireItemId(), node);
        }

        List<ExperimentQuestionnaireItemResponse> tree = new ArrayList<>();

        // 遍历节点列表，将每个节点放入对应父节点的children中
        for (ExperimentQuestionnaireItemResponse node : nodes) {
            String parentId = node.getExperimentQuestionnaireItemPid();
            if ("0".equals(parentId)) {
                // 根节点
                tree.add(node);
            } else {
                ExperimentQuestionnaireItemResponse parent = nodeMap.get(parentId);
                if (parent != null) {
                    parent.addChild(node);
                }
            }
        }

        return tree;
    }
}
