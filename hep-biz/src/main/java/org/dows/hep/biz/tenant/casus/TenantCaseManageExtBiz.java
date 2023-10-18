package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dows.account.api.*;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.request.AccountOrgGeoRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgGeoResponse;
import org.dows.account.response.AccountOrgInfoResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.request.CaseInstanceCopyRequest;
import org.dows.hep.biz.base.indicator.RsUtilBiz;
import org.dows.hep.biz.base.person.PersonManageExtBiz;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.dows.hep.biz.base.indicator.CaseIndicatorInstanceExtBiz.checkNullNewId;
import static org.dows.hep.biz.base.org.OrgBiz.createCode;
import static org.dows.hep.biz.constant.CaseBizConstants.*;

/**
 * 案例 复制
 * 1.管理员端自定义权限：
 * ●可以查看、编辑、复制、删除系统中管理员用户创建的已发布/未发布的社区；
 * ●可以查看、编辑、复制、删除系统中所有教师用户创建的已发布/未发布的社区
 * 2.教师端自定义权限：
 * ●可以查看、编辑、复制、删除自己创建的已发布/未发布的社区；
 * ●可以查看管理员或其他教师创建的已经发布的社区；
 * ●可以复制所有已经发布的社区，且复制生成的社区其创建者为该用户；
 * ●不能删除管理员或其他教师创建的社区；
 *
 * @description: lifel 2023/10/13
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class TenantCaseManageExtBiz {

    private final CaseInstanceService caseInstanceService;
    private final TenantCaseBaseBiz baseBiz;
    private final RsUtilBiz rsUtilBiz;
    private final IdGenerator idGenerator;
    private final TenantCaseNoticeBiz caseNoticeBiz;
    private final CaseSchemeService caseSchemeService;
    private final QuestionSectionService questionSectionService;
    private final QuestionInstanceService questionInstanceService;
    private final QuestionSectionItemService questionSectionItemService;
    private final QuestionSectionDimensionService questionSectionDimensionService;
    private final CaseOrgService caseOrgService;
    private final CaseOrgFeeService caseOrgFeeService;
    private final CaseOrgModuleService caseOrgModuleService;
    private final CaseOrgModuleFuncRefService caseOrgModuleFuncRefService;
    private final CasePersonService casePersonService;
    private final PersonManageExtBiz personManageExtBiz;
    private final AccountOrgGeoApi accountOrgGeoApi;
    private final AccountOrgApi accountOrgApi;
    private final CaseQuestionnaireService caseQuestionnaireService;
    private final CaseOrgQuestionnaireService caseOrgQuestionnaireService;
    private final CaseSettingService caseSettingService;
    private final AccountGroupApi accountGroupApi;
    private final AccountUserApi accountUserApi;
    private final AccountInstanceApi accountInstanceApi;
    private final QuestionAnswersService questionAnswersService;
    private final QuestionOptionsService questionOptionsService;

    /**
     * TODO 案例 复制
     */
    @DSTransactional
    public String duplicateCaseInstance(CaseInstanceCopyRequest request) throws ExecutionException, InterruptedException {
        //需要复制的案例id
        String oriCaseInstanceId = request.getOriCaseInstanceId();
        String targetCaseInstanceName = request.getTargetCaseInstanceName();
        String appId = request.getAppId();
        if (StrUtil.isBlank(oriCaseInstanceId) || StrUtil.isBlank(targetCaseInstanceName)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }

        // copy base-info
        CaseInstanceEntity caseInstanceEntity = copyCaseInstance0(oriCaseInstanceId, targetCaseInstanceName);
        String newCaseInstanceId = caseInstanceEntity.getCaseInstanceId();
        //  copy case-scheme
//        caseSchemeBiz.copyCaseScheme(oriCaseInstanceId, caseInstanceEntity);
        try {
            duplicateCaseScheme(oriCaseInstanceId, newCaseInstanceId);
        } catch (ExecutionException | InterruptedException e) {
            log.error("TenantCaseManageExtBiz.duplicateCaseInstance duplicateCaseScheme error");
            throw new RuntimeException(e);
        }
        //  copy case-org  oldOrgId<-> newOrgId
        Map<String, String> kOldIdVNewIdMap = duplicateCaseOrgList(oriCaseInstanceId, newCaseInstanceId, request.getIsCopyPerson(), appId);
        // copy case-notice
        caseNoticeBiz.copyCaseNotice(oriCaseInstanceId, caseInstanceEntity);
        //  copy case-questionnaire
        duplicateCaseQuestionnaire(oriCaseInstanceId, newCaseInstanceId, kOldIdVNewIdMap);

        return newCaseInstanceId;
    }

    /**
     * 复制 案例-知识答题
     * kOldIdVNewIdMap：机构id和新机构id对应
     */
    private void duplicateCaseQuestionnaire(String oriCaseInstanceId, String newCaseInstanceId,
                                            Map<String, String> kOldOrgIdVNewOrgIdMap) throws ExecutionException, InterruptedException {
        //案例知识答题分配方式设置
        CaseSettingEntity caseSetting = getCaseSetting(oriCaseInstanceId);
        if (Objects.isNull(caseSetting)) {
            return;
        }
        //试卷
        List<CaseQuestionnaireEntity> caseQuestionnaireList = getCaseQuestionnaire(oriCaseInstanceId);
        Set<String> questionSectionIdSet = caseQuestionnaireList.stream().map(CaseQuestionnaireEntity::getQuestionSectionId).collect(Collectors.toSet());

        //试卷选中的题目
        List<QuestionSectionItemEntity> questionSectionItemList = getQuestionItemByQuestionSectionId(questionSectionIdSet);
        Set<String> questionInstanceIdSet = questionSectionItemList.stream().map(QuestionSectionItemEntity::getQuestionInstanceId).collect(Collectors.toSet());

        //试题实例
        List<QuestionInstanceEntity> questionInstanceList = getQuestionInstanceList(questionInstanceIdSet);

        //试题答案
        List<QuestionAnswersEntity> questionAnswersList = getQuestionAnswersList(questionInstanceIdSet);

        Set<String> questionOptionsIdSet = questionAnswersList.stream().map(QuestionAnswersEntity::getQuestionOptionsId).collect(Collectors.toSet());
        //试题选项
        List<QuestionOptionsEntity> questionOptionsList = getQuestionOptionsList1(questionInstanceIdSet);

        //试卷与机构分配关系
        List<CaseOrgQuestionnaireEntity> caseOrgQuestionnaireList = getCaseOrgQuestionnaire(oriCaseInstanceId);

        questionInstanceIdSet.addAll(questionSectionIdSet);
        questionInstanceIdSet.addAll(questionOptionsIdSet);
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() ->
                rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, questionInstanceIdSet));
        cfPopulateKOldIdVNewIdMap.get();
        kOldIdVNewIdMap.putAll(kOldOrgIdVNewOrgIdMap);

        CompletableFuture<Void> getQuestionOptionsListCF = CompletableFuture.runAsync(() -> {
            getQuestionOptionsList(questionOptionsList, kOldIdVNewIdMap);
            questionOptionsService.saveOrUpdateBatch(questionOptionsList);
        });

        CompletableFuture<Void> getQuestionAnswersListCF = CompletableFuture.runAsync(() -> {
            getQuestionAnswersList(questionAnswersList, kOldIdVNewIdMap);
            questionAnswersService.saveOrUpdateBatch(questionAnswersList);
        });

        CompletableFuture<Void> getQuestionInstanceListCF = CompletableFuture.runAsync(() -> {
            getAllQuestionInstanceList(questionInstanceList, kOldIdVNewIdMap);
            questionInstanceService.saveOrUpdateBatch(questionInstanceList);
        });

        CompletableFuture<Void> getQuestionSectionItemListCF = CompletableFuture.runAsync(() -> {
            getQuestionItemList(questionSectionItemList, kOldIdVNewIdMap);
            questionSectionItemService.saveOrUpdateBatch(questionSectionItemList);
        });

        CompletableFuture<Void> getCaseQuestionnaireListCF = CompletableFuture.runAsync(() -> {
            getCaseQuestionnaireList(caseQuestionnaireList, newCaseInstanceId, kOldIdVNewIdMap);
            caseQuestionnaireService.saveOrUpdateBatch(caseQuestionnaireList);
        });

        CompletableFuture<Void> getCaseOrgQuestionnaireListCF = CompletableFuture.runAsync(() -> {
            getCaseOrgQuestionnaireList(caseOrgQuestionnaireList, newCaseInstanceId, kOldIdVNewIdMap);
            caseOrgQuestionnaireService.saveOrUpdateBatch(caseOrgQuestionnaireList);
        });

        CompletableFuture.allOf(getQuestionOptionsListCF, getQuestionAnswersListCF, getQuestionInstanceListCF,
                getQuestionSectionItemListCF, getCaseQuestionnaireListCF, getCaseOrgQuestionnaireListCF).get();

        caseSetting.setId(null);
        caseSetting.setDt(new Date());
        caseSetting.setCaseSettingId(idGenerator.nextIdStr());
        caseSetting.setCaseInstanceId(newCaseInstanceId);
        caseSettingService.save(caseSetting);


    }

    /**
     * 复制 案例机构人物
     */
    private void duplicateCaseOrgPersonList(String oriCaseInstanceId, String newCaseInstanceId, Map<String, String> kOldIdVNewIdMap) throws ExecutionException, InterruptedException {
        List<CaseOrgEntity> caseOrgList = getCaseOrgList(oriCaseInstanceId);
        //没有机构
        if (CollectionUtils.isEmpty(caseOrgList)) {
            return;
        }
        List<CasePersonEntity> casePersonList = getCasePersonList(oriCaseInstanceId);
        //案例机构id对应人员
        Map<String, List<CasePersonEntity>> kCaseOrgIdVPerson = casePersonList.stream().collect(Collectors.groupingBy(CasePersonEntity::getCaseOrgId));
        for (CaseOrgEntity caseOrg : caseOrgList) {
            //机构人员
            String caseOrgId = caseOrg.getCaseOrgId();
            String newCaseOrgId = checkNullNewId(caseOrgId, kOldIdVNewIdMap);
            List<CasePersonEntity> casePersonEntityList = kCaseOrgIdVPerson.get(caseOrgId);
            //机构里没有人物
            if (CollectionUtils.isEmpty(casePersonEntityList)) {
                continue;
            }
            casePersonEntityList.forEach(casePerson -> {
                String oldAccountId = casePerson.getAccountId();
                PersonInstanceResponse personInstanceResponse = personManageExtBiz.duplicatePerson(oldAccountId, ORG_PERSON);
                if (personInstanceResponse == null || StringUtils.isEmpty(personInstanceResponse.getAccountId())) {
                    throw new BizException("复制人物异常");
                }
                String newAccountId = personInstanceResponse.getAccountId();
                addPersonToCaseOrg(newAccountId, newCaseOrgId, APPId);
                kOldIdVNewIdMap.put(oldAccountId, newAccountId);
            });
        }
        getCasePersonList(casePersonList, newCaseInstanceId, kOldIdVNewIdMap);
        casePersonService.saveOrUpdateBatch(casePersonList);
    }

    /**
     * 建立机构人物关系
     */
    public void addPersonToCaseOrg(String newPersonId, String newCaseOrgId, String appId) {
        //1、建立人物与组关系
        //1.1、通过案例机构ID找到机构ID
        CaseOrgEntity newEntity = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseOrgId, newCaseOrgId)
                .eq(CaseOrgEntity::getDeleted, false)
                .eq(CaseOrgEntity::getAppId, appId)
                .one();
        //1.2、账户实例
        AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(newPersonId);
        //1.3、获取用户ID
        String userId = accountUserApi.getUserByAccountId(newPersonId).getUserId();
        accountGroupApi.insertAccountGroup(AccountGroupRequest.builder()
                .groupId(idGenerator.nextIdStr())
                .orgId(newEntity.getOrgId())
                .orgName(newEntity.getOrgName())
                .accountId(newPersonId)
                .accountName(instanceResponse.getAccountName())
                .userId(userId)
                .appId(appId)
                .build());
    }

    /**
     * 复制机构列表
     * isCopyPerson true/false
     * true 机构人物一起复制
     */
    private Map<String, String> duplicateCaseOrgList(String oriCaseInstanceId, String newCaseInstanceId,
                                                     boolean isCopyPerson, String appId) throws ExecutionException, InterruptedException {
        List<CaseOrgEntity> caseOrgList = getCaseOrgList(oriCaseInstanceId);
        //案例机构id
        Set<String> caseOrgIdSet = caseOrgList.stream().map(CaseOrgEntity::getCaseOrgId).collect(Collectors.toSet());

        List<CaseOrgFeeEntity> caseOrgFeeList = getCaseOrgFeeList(caseOrgIdSet);

        List<CaseOrgModuleEntity> caseOrgModuleList = getCaseOrgModuleList(caseOrgIdSet);
        //机构功能id
        Set<String> caseOrgModuleIdSet = caseOrgModuleList.stream().map(CaseOrgModuleEntity::getCaseOrgModuleId).collect(Collectors.toSet());

        List<CaseOrgModuleFuncRefEntity> caseOrgModuleFuncRefList = getCaseOrgModuleFuncRefList(caseOrgModuleIdSet);

        //机构 oldOrgId 和 newOrgId 对应
        Map<String, String> kOldOrgIdVNewOrgIdMap = new HashMap<>();

        Set<String> allOldIdSet = new HashSet<>();
        allOldIdSet.addAll(caseOrgIdSet);
        allOldIdSet.addAll(caseOrgModuleIdSet);
        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() ->
                rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allOldIdSet));
        cfPopulateKOldIdVNewIdMap.get();

        //机构
        caseOrgList.forEach(caseOrg -> {
            String orgId = caseOrg.getOrgId();
            try {
                kOldOrgIdVNewOrgIdMap.putAll(duplicateOrg(orgId, appId));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        //保存案例机构
        getCaseOrgList(caseOrgList, newCaseInstanceId, kOldIdVNewIdMap, kOldOrgIdVNewOrgIdMap);
        caseOrgService.saveOrUpdateBatch(caseOrgList);

        //复制案例机构人物
        if (isCopyPerson) {
            duplicateCaseOrgPersonList(oriCaseInstanceId, newCaseInstanceId, kOldIdVNewIdMap);
        }

        CompletableFuture<Void> getCaseOrgFeeListCF = CompletableFuture.runAsync(() -> {
            getCaseOrgFeeList(caseOrgFeeList, newCaseInstanceId, kOldIdVNewIdMap);
            caseOrgFeeService.saveOrUpdateBatch(caseOrgFeeList);
        });

        CompletableFuture<Void> getCaseOrgModuleListCF = CompletableFuture.runAsync(() -> {
            getCaseOrgModuleList(caseOrgModuleList, kOldIdVNewIdMap);
            caseOrgModuleService.saveOrUpdateBatch(caseOrgModuleList);
        });

        CompletableFuture<Void> getCaseOrgModuleFuncRefListCF = CompletableFuture.runAsync(() -> {
            getCaseOrgModuleFuncRefList(caseOrgModuleFuncRefList, kOldIdVNewIdMap);
            caseOrgModuleFuncRefService.saveOrUpdateBatch(caseOrgModuleFuncRefList);
        });

        CompletableFuture.allOf(getCaseOrgFeeListCF, getCaseOrgModuleListCF, getCaseOrgModuleFuncRefListCF).get();
        return kOldOrgIdVNewOrgIdMap;
    }

    /**
     * 去uim查询复制 机构并返回 newOrgId
     */
    private Map<String, String> duplicateOrg(String orgId, String appId) throws Exception {
        Map<String, String> kOldOrgIdVNewOrgIdMap = new HashMap<>();
        //1、获取机构实例
        AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(orgId, appId);
        //2、获取机构基本信息
        AccountOrgInfoResponse orgInfoResponse = accountOrgApi.getAccountOrgInfoByOrgId(orgId);
        //orgCode不能重复
        String orgCode = createCode(7);
        String newOrgId = accountOrgApi.createAccountOrg(AccountOrgRequest.builder()
                .orgCode(orgCode)
                .orgName(orgResponse.getOrgName())
                .descr(orgResponse.getDescr())
                .profile(orgResponse.getProfile())
                .orgType(orgResponse.getOrgType())
                .status(orgResponse.getStatus())
                .dt(new Date())
                .operationManual(orgInfoResponse.getOperationManual())
                .isEnable(orgInfoResponse.getIsEnable())
                .appId(appId).build());
        kOldOrgIdVNewOrgIdMap.put(orgId, newOrgId);

        //3、获取机构地理位置信息
        AccountOrgGeoResponse orgGeoResponse = accountOrgGeoApi.getAccountOrgInfoByOrgId(orgId);
        accountOrgGeoApi.insertOrgGeo(AccountOrgGeoRequest
                .builder()
                .orgId(newOrgId)
                .orgName(orgResponse.getOrgName())
                .orgLongitude(orgGeoResponse.getOrgLongitude())
                .orgLatitude(orgGeoResponse.getOrgLatitude())
                .build());
        return kOldOrgIdVNewOrgIdMap;
    }

    /**
     * 复制 案例方案设计
     */
    private void duplicateCaseScheme(String oriCaseInstanceId, String newCaseInstanceId) throws ExecutionException, InterruptedException {
        CaseSchemeEntity oriEntity = getByInstanceId(oriCaseInstanceId);
        //问卷id
        String questionSectionId = oriEntity.getQuestionSectionId();
        //问卷
        QuestionSectionEntity questionSection = getByQuestionSectionId(questionSectionId);

        //题目
        List<QuestionSectionItemEntity> questionItemList = getQuestionItemByQuestionSectionId(questionSectionId);
        //一级问题实例
        Set<String> questionInstanceIdSet = questionItemList.stream().map(QuestionSectionItemEntity::getQuestionInstanceId).collect(Collectors.toSet());
        List<QuestionInstanceEntity> allQuestionInstanceList = getAllQuestionInstanceList(questionInstanceIdSet);
        //所有问题实例id
        Set<String> allQuestionInstanceIdSet = allQuestionInstanceList.stream().map(QuestionInstanceEntity::getQuestionInstanceId).collect(Collectors.toSet());
        //评分维度
        List<QuestionSectionDimensionEntity> questionDimensionList = getQuestionDimensionByQuestionSectionId(questionSectionId);

        //加入问卷id
        allQuestionInstanceIdSet.add(questionSectionId);
        //生成新的id
        Map<String, String> kOldIdVNewIdMap = new HashMap<>();
        CompletableFuture<Void> cfPopulateKOldIdVNewIdMap = CompletableFuture.runAsync(() ->
                rsUtilBiz.populateKOldIdVNewIdMap(kOldIdVNewIdMap, allQuestionInstanceIdSet));
        cfPopulateKOldIdVNewIdMap.get();

        CompletableFuture<Void> getQuestionItemListCF = CompletableFuture.runAsync(() ->{
                getQuestionItemList(questionItemList, kOldIdVNewIdMap);
            questionSectionItemService.saveOrUpdateBatch(questionItemList);
        });

        CompletableFuture<Void> getAllQuestionInstanceListCF = CompletableFuture.runAsync(() ->{
                getAllQuestionInstanceList(allQuestionInstanceList, kOldIdVNewIdMap);
            questionInstanceService.saveOrUpdateBatch(allQuestionInstanceList);
        });

        CompletableFuture<Void> getQuestionDimensionListCF = CompletableFuture.runAsync(() ->{
                getQuestionDimensionList(questionDimensionList, kOldIdVNewIdMap.get(questionSectionId));
            questionSectionDimensionService.saveOrUpdateBatch(questionDimensionList);
        });

        CaseSchemeEntity targetCaseScheme = BeanUtil.copyProperties(oriEntity, CaseSchemeEntity.class);
        targetCaseScheme.setId(null);
        targetCaseScheme.setDt(new Date());
        targetCaseScheme.setCaseSchemeId(baseBiz.getIdStr());
        targetCaseScheme.setCaseInstanceId(newCaseInstanceId);
        targetCaseScheme.setQuestionSectionId(kOldIdVNewIdMap.get(questionSectionId));
        caseSchemeService.save(targetCaseScheme);
        questionSection.setId(null);
        questionSection.setDt(new Date());
        questionSection.setQuestionSectionId(kOldIdVNewIdMap.get(questionSectionId));
        questionSectionService.save(questionSection);

        CompletableFuture.allOf(getQuestionItemListCF,getAllQuestionInstanceListCF,getQuestionDimensionListCF).get();
    }

    private void getCasePersonList(List<CasePersonEntity> casePersonList,
                                   String newCaseInstanceId, Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(casePersonList, kOldIdVNewIdMap)) {
            return;
        }
        casePersonList.forEach(casePerson -> {
            casePerson.setCasePersonId(baseBiz.getIdStr());
            casePerson.setCaseInstanceId(newCaseInstanceId);
            casePerson.setCaseOrgId(checkNullNewId(casePerson.getCaseOrgId(), kOldIdVNewIdMap));
            casePerson.setAccountId(checkNullNewId(casePerson.getAccountId(), kOldIdVNewIdMap));
            casePerson.setId(null);
            casePerson.setDt(new Date());
        });

    }

    private void getCaseOrgModuleFuncRefList(List<CaseOrgModuleFuncRefEntity> caseOrgModuleFuncRefList,
                                             Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseOrgModuleFuncRefList, kOldIdVNewIdMap)) {
            return;
        }
        caseOrgModuleFuncRefList.forEach(caseOrgModuleRef -> {
            caseOrgModuleRef.setCaseOrgModuleFuncRefId(baseBiz.getIdStr());
            caseOrgModuleRef.setCaseOrgModuleId(checkNullNewId(caseOrgModuleRef.getCaseOrgModuleId(), kOldIdVNewIdMap));
            caseOrgModuleRef.setId(null);
            caseOrgModuleRef.setDt(new Date());
        });
    }

    private void getCaseOrgModuleList(List<CaseOrgModuleEntity> caseOrgModuleList,
                                      Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseOrgModuleList, kOldIdVNewIdMap)) {
            return;
        }
        caseOrgModuleList.forEach(caseOrgModule -> {
            caseOrgModule.setCaseOrgId(checkNullNewId(caseOrgModule.getCaseOrgId(), kOldIdVNewIdMap));
            caseOrgModule.setCaseOrgModuleId(checkNullNewId(caseOrgModule.getCaseOrgModuleId(), kOldIdVNewIdMap));
            caseOrgModule.setId(null);
            caseOrgModule.setDt(new Date());
        });
    }

    private void getCaseOrgFeeList(List<CaseOrgFeeEntity> caseOrgFeeList,
                                   String newCaseInstanceId, Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseOrgFeeList, kOldIdVNewIdMap, newCaseInstanceId)) {
            return;
        }
        caseOrgFeeList.forEach(caseOrgFee -> {
            caseOrgFee.setCaseOrgId(checkNullNewId(caseOrgFee.getCaseOrgId(), kOldIdVNewIdMap));
            caseOrgFee.setCaseOrgFeeId(baseBiz.getIdStr());
            caseOrgFee.setCaseInstanceId(newCaseInstanceId);
            caseOrgFee.setId(null);
            caseOrgFee.setDt(new Date());
        });
    }

    private void getCaseOrgList(List<CaseOrgEntity> caseOrgList, String newCaseInstanceId,
                                Map<String, String> kOldIdVNewIdMap, Map<String, String> kOldOrgIdVNewOrgIdMap
    ) {
        if (checkNull(caseOrgList, kOldIdVNewIdMap, newCaseInstanceId)) {
            return;
        }
        caseOrgList.forEach(caseOrg -> {
            caseOrg.setCaseOrgId(checkNullNewId(caseOrg.getCaseOrgId(), kOldIdVNewIdMap));
            caseOrg.setCaseInstanceId(newCaseInstanceId);
            caseOrg.setOrgId(checkNullNewId(caseOrg.getOrgId(), kOldOrgIdVNewOrgIdMap));
            caseOrg.setId(null);
            caseOrg.setDt(new Date());
        });
    }

    private void getCaseOrgQuestionnaireList(List<CaseOrgQuestionnaireEntity> caseOrgQuestionnaireList,
                                             String newCaseInstanceId, Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(caseOrgQuestionnaireList, kOldIdVNewIdMap, newCaseInstanceId)) {
            return;
        }
        caseOrgQuestionnaireList.forEach(caseOrgQuestionnaire -> {
            caseOrgQuestionnaire.setCaseOrgQuestionnaireId(baseBiz.getIdStr());
            caseOrgQuestionnaire.setCaseQuestionnaireId(checkNullNewId(caseOrgQuestionnaire.getCaseQuestionnaireId(), kOldIdVNewIdMap));
            caseOrgQuestionnaire.setCaseInstanceId(newCaseInstanceId);
            caseOrgQuestionnaire.setCaseOrgId(checkNullNewId(caseOrgQuestionnaire.getCaseOrgId(), kOldIdVNewIdMap));
            caseOrgQuestionnaire.setDt(new Date());
            caseOrgQuestionnaire.setId(null);
        });
    }

    private void getCaseQuestionnaireList(List<CaseQuestionnaireEntity> caseQuestionnaireList,
                                          String newCaseInstanceId, Map<String, String> kOldIdVNewIdMap) {
        if (CollectionUtils.isEmpty(caseQuestionnaireList)) {
            return;
        }
        caseQuestionnaireList.forEach(caseQuestionnaire -> {
            caseQuestionnaire.setId(null);
            caseQuestionnaire.setDt(new Date());
            caseQuestionnaire.setCaseQuestionnaireId(baseBiz.getIdStr());
            caseQuestionnaire.setCaseInstanceId(newCaseInstanceId);
            caseQuestionnaire.setQuestionSectionId(checkNullNewId(caseQuestionnaire.getQuestionSectionId(), kOldIdVNewIdMap));
        });
    }

    private void getQuestionDimensionList(List<QuestionSectionDimensionEntity> questionDimensionList,
                                          String newQuestionSectionId) {
        if (CollectionUtils.isEmpty(questionDimensionList)) {
            return;
        }
        questionDimensionList.forEach(questionDimension -> {
            questionDimension.setId(null);
            questionDimension.setDt(new Date());
            questionDimension.setQuestionSectionDimensionId(idGenerator.nextIdStr());
            questionDimension.setQuestionSectionId(newQuestionSectionId);
        });
    }

    private void getQuestionOptionsList(List<QuestionOptionsEntity> questionOptionsList,
                                        Map<String, String> kOldIdVNewIdMap) {
        questionOptionsList.forEach(options -> {
            options.setId(null);
            options.setDt(new Date());
            options.setQuestionOptionsId(checkNullNewId(options.getQuestionOptionsId(), kOldIdVNewIdMap));
            options.setQuestionInstanceId(checkNullNewId(options.getQuestionInstanceId(), kOldIdVNewIdMap));
        });
    }

    private void getQuestionAnswersList(List<QuestionAnswersEntity> questionAnswersList,
                                        Map<String, String> kOldIdVNewIdMap) {
        questionAnswersList.forEach(answers -> {
            answers.setId(null);
            answers.setDt(new Date());
            answers.setQuestionAnswerId(idGenerator.nextIdStr());
            answers.setQuestionOptionsId(checkNullNewId(answers.getQuestionOptionsId(), kOldIdVNewIdMap));
            answers.setQuestionInstanceId(checkNullNewId(answers.getQuestionInstanceId(), kOldIdVNewIdMap));
        });
    }

    private void getAllQuestionInstanceList(List<QuestionInstanceEntity> allQuestionInstanceList,
                                            Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(allQuestionInstanceList, kOldIdVNewIdMap)) {
            return;
        }
        allQuestionInstanceList.forEach(questionInstance -> {
            questionInstance.setId(null);
            questionInstance.setDt(new Date());
            questionInstance.setQuestionInstanceId(checkNullNewId(questionInstance.getQuestionInstanceId(), kOldIdVNewIdMap));
            questionInstance.setQuestionInstancePid(checkNullNewId(questionInstance.getQuestionInstancePid(), kOldIdVNewIdMap));
        });
    }

    private void getQuestionItemList(List<QuestionSectionItemEntity> questionItemList,
                                     Map<String, String> kOldIdVNewIdMap) {
        if (checkNull(questionItemList, kOldIdVNewIdMap)) {
            return;
        }
        questionItemList.forEach(questionItem -> {
            questionItem.setId(null);
            questionItem.setDt(new Date());
            questionItem.setQuestionSectionItemId(idGenerator.nextIdStr());
            questionItem.setQuestionSectionId(checkNullNewId(questionItem.getQuestionSectionId(), kOldIdVNewIdMap));
            questionItem.setQuestionInstanceId(checkNullNewId(questionItem.getQuestionInstanceId(), kOldIdVNewIdMap));
        });
        System.out.println(questionItemList);
    }

    private boolean checkNull(Object oldList, Map<String, String> kOldIdVNewIdMap, String newId) {
        return StringUtils.isBlank(newId) || checkNull(oldList, kOldIdVNewIdMap);
    }

    private boolean checkNull(Object oldList, Map<String, String> kOldIdVNewIdMap) {
        return Objects.isNull(oldList) || Objects.isNull(kOldIdVNewIdMap);
    }


    //获取所有问题实例
    public List<QuestionInstanceEntity> getAllQuestionInstanceList(Set<String> questionInstanceIdSet) {
        List<QuestionInstanceEntity> result = new ArrayList<>();
        result.addAll(getQuestionInstanceList(questionInstanceIdSet));
        result.addAll(recursionQuestionInstanceSonList(questionInstanceIdSet));
        return result;
    }

    public List<QuestionInstanceEntity> recursionQuestionInstanceSonList(Set<String> questionInstancePIdSet) {
        List<QuestionInstanceEntity> result = new ArrayList<>();
        while (!CollectionUtils.isEmpty(questionInstancePIdSet)) {
            List<QuestionInstanceEntity> questionInstanceSonList = getQuestionInstanceSonList(questionInstancePIdSet);
            result.addAll(questionInstanceSonList);
            questionInstancePIdSet = questionInstanceSonList.stream()
                    .map(QuestionInstanceEntity::getQuestionInstanceId)
                    .collect(Collectors.toSet());
        }
        return result;
    }


    //知识答题
    //查询试卷分配设置
    public CaseSettingEntity getCaseSetting(String caseInstanceId) {
        return caseSettingService.lambdaQuery()
                .eq(CaseSettingEntity::getCaseInstanceId, caseInstanceId).one();
    }

    //获取试卷机构分配
    public List<CaseOrgQuestionnaireEntity> getCaseOrgQuestionnaire(String caseInstanceId) {
        return caseOrgQuestionnaireService.lambdaQuery()
                .eq(CaseOrgQuestionnaireEntity::getCaseInstanceId, caseInstanceId).list();
    }

    //查询题型和数量
    public List<CaseQuestionnaireEntity> getCaseQuestionnaire(String caseInstanceId) {
        return caseQuestionnaireService.lambdaQuery()
                .eq(CaseQuestionnaireEntity::getCaseInstanceId, caseInstanceId)
                .list();
    }

    //批量查询机构人物集合
    public List<CasePersonEntity> getCasePersonList(String caseInstanceId) {
        return casePersonService.lambdaQuery()
                .eq(CasePersonEntity::getCaseInstanceId, caseInstanceId).list();
    }

    //查询机构功能映射
    public List<CaseOrgModuleFuncRefEntity> getCaseOrgModuleFuncRefList(Set<String> caseOrgModuleIdSet) {
        return caseOrgModuleFuncRefService.lambdaQuery()
                .in(CaseOrgModuleFuncRefEntity::getCaseOrgModuleId, caseOrgModuleIdSet).list();
    }

    //查询机构功能
    public List<CaseOrgModuleEntity> getCaseOrgModuleList(Set<String> caseOrgIdSet) {
        return caseOrgModuleService.lambdaQuery()
                .in(CaseOrgModuleEntity::getCaseOrgId, caseOrgIdSet).list();
    }

    //查询机构费用以及报销比例
    public List<CaseOrgFeeEntity> getCaseOrgFeeList(Set<String> caseOrgIdSet) {
        return caseOrgFeeService.lambdaQuery()
                .in(CaseOrgFeeEntity::getCaseOrgId, caseOrgIdSet).list();
    }

    //查询案例所属机构
    public List<CaseOrgEntity> getCaseOrgList(String caseInstanceId) {
        return caseOrgService.lambdaQuery()
                .in(CaseOrgEntity::getCaseInstanceId, caseInstanceId).list();
    }

    public List<QuestionOptionsEntity> getQuestionOptionsList1(Set<String> questionInstanceIdSet) {
        return questionOptionsService.lambdaQuery().in(QuestionOptionsEntity::getQuestionInstanceId, questionInstanceIdSet).list();
    }

    public List<QuestionOptionsEntity> getQuestionOptionsList0(Set<String> questionOptionsIdSet) {
        return questionOptionsService.lambdaQuery().in(QuestionOptionsEntity::getQuestionOptionsId, questionOptionsIdSet).list();
    }

    public List<QuestionAnswersEntity> getQuestionAnswersList(Set<String> questionInstanceIdSet) {
        return questionAnswersService.lambdaQuery()
                .in(QuestionAnswersEntity::getQuestionInstanceId, questionInstanceIdSet).list();
    }

    /**
     * 试题实例查询
     */
    public List<QuestionInstanceEntity> getQuestionInstanceList(Set<String> questionInstanceIdSet) {
        return questionInstanceService.lambdaQuery()
                .in(QuestionInstanceEntity::getQuestionInstanceId, questionInstanceIdSet).list();
    }

    //问题子集
    public List<QuestionInstanceEntity> getQuestionInstanceSonList(Set<String> questionInstancePIdSet) {
        return questionInstanceService.lambdaQuery()
                .in(QuestionInstanceEntity::getQuestionInstancePid, questionInstancePIdSet).list();
    }

    //查询评分维度
    public List<QuestionSectionDimensionEntity> getQuestionDimensionByQuestionSectionId(String questionSectionId) {
        return questionSectionDimensionService.lambdaQuery()
                .eq(QuestionSectionDimensionEntity::getQuestionSectionId, questionSectionId).list();
    }

    //查询题目集
    public List<QuestionSectionItemEntity> getQuestionItemByQuestionSectionId(String questionSectionId) {
        return questionSectionItemService.lambdaQuery()
                .eq(QuestionSectionItemEntity::getQuestionSectionId, questionSectionId).list();
    }

    public List<QuestionSectionItemEntity> getQuestionItemByQuestionSectionId(Set<String> questionSectionIdSet) {
        return questionSectionItemService.lambdaQuery()
                .in(QuestionSectionItemEntity::getQuestionSectionId, questionSectionIdSet).list();
    }

    //查询问题集
    public QuestionSectionEntity getByQuestionSectionId(String questionSectionId) {
        return questionSectionService.lambdaQuery()
                .eq(QuestionSectionEntity::getQuestionSectionId, questionSectionId).one();
    }

    //查询方案设计
    public CaseSchemeEntity getByInstanceId(String caseInstanceId) {
        return caseSchemeService.lambdaQuery()
                .eq(CaseSchemeEntity::getCaseInstanceId, caseInstanceId).one();
    }

    /**
     * 案例基础信息
     */
    private CaseInstanceEntity copyCaseInstance0(String oriCaseInstanceId, String caseInstanceName) {
        // get ori
        CaseInstanceEntity oriEntity = getById(oriCaseInstanceId);
        if (StringUtils.isBlank(caseInstanceName)) {
            caseInstanceName = oriEntity.getCaseName() + NAME_SUFFIX;
        }
        // copy
        CaseInstanceEntity newEntity = BeanUtil.copyProperties(oriEntity, CaseInstanceEntity.class);
        newEntity.setId(null);
        newEntity.setDt(new Date());
        newEntity.setCaseInstanceId(baseBiz.getIdStr());
        newEntity.setCaseIdentifier(baseBiz.getIdStr());
        newEntity.setVer(baseBiz.getLastVer());
        newEntity.setCaseName(caseInstanceName);
        caseInstanceService.save(newEntity);
        return newEntity;
    }

    /**
     * 查询案例基本信息
     */
    public CaseInstanceEntity getById(String caseInstanceId) {
        LambdaQueryWrapper<CaseInstanceEntity> queryWrapper = new LambdaQueryWrapper<CaseInstanceEntity>()
                .eq(CaseInstanceEntity::getCaseInstanceId, caseInstanceId);
        return caseInstanceService.getOne(queryWrapper);
    }
}
