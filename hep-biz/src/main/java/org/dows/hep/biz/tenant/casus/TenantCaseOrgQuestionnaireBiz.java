package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.CasePeriodsEnum;
import org.dows.hep.api.tenant.casus.request.CaseOrgQuestionnaireRequest;
import org.dows.hep.api.tenant.casus.request.CaseQuestionnaireSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseOrgQuestionnaireResponse;
import org.dows.hep.api.tenant.casus.response.CaseQuestionnaireResponse;
import org.dows.hep.api.user.organization.request.CaseOrgRequest;
import org.dows.hep.api.user.organization.response.CaseOrgResponse;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.entity.CaseOrgQuestionnaireEntity;
import org.dows.hep.service.CaseOrgQuestionnaireService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @description
 * @date 2023/5/25 10:53
 */
@RequiredArgsConstructor
@Service
public class TenantCaseOrgQuestionnaireBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final TenantCaseQuestionnaireBiz caseQuestionnaireBiz;
    private final CaseOrgQuestionnaireService orgQuestionnaireService;
    private final OrgBiz orgBiz;

    /**
     * @param
     * @return
     * @author fhb
     * @description 列出未选择的问卷
     * @date 2023/5/25 11:43
     */
    public List<List<CaseQuestionnaireResponse>> listUnselectedQuestionnaires(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        Map<String, List<CaseQuestionnaireResponse>> result = new LinkedHashMap<>();
        Arrays.stream(CasePeriodsEnum.values()).forEach(item -> {
            result.put(item.getName(), new ArrayList<>());
        });

        // 获取案例下问卷
        List<CaseQuestionnaireResponse> questionnaireList = listCaseQuestionnaire(caseInstanceId);

        // 剔除已经被选中的
        filterUnselectedQuestionnaire(caseInstanceId, questionnaireList);

        // 根据期数分组
        Map<String, List<CaseQuestionnaireResponse>> periodCollect = questionnaireList.stream().collect(Collectors.groupingBy(CaseQuestionnaireResponse::getPeriods));
        result.forEach((period, list) -> {
            List<CaseQuestionnaireResponse> responses = periodCollect.get(period);
            if (Objects.nonNull(responses)) {
                result.replace(period, responses);
            }
        });

        return result.values().stream().toList();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 列出已经选择的问卷
     * @date 2023/5/25 11:43
     */
    public List<Map<String, CaseOrgQuestionnaireResponse>> listSelectedQuestionnaires(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        // 期数 collect
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> result = new LinkedHashMap<>();
        Arrays.stream(CasePeriodsEnum.values()).forEach(item -> {
            result.put(item.getName(), new HashMap<>());
        });

        List<CaseOrgResponse> orgList = listOrgOfCaseInstance(caseInstanceId);
        if (Objects.isNull(orgList) || orgList.isEmpty()) {
            return result.values().stream().toList();
        }

        // 机构 collect
        result.forEach((period, orgMap) -> {
            Map<String, CaseOrgQuestionnaireResponse> orgCollect = new HashMap<>();
            orgList.forEach(org -> {
                orgCollect.put(org.getOrgName(), new CaseOrgQuestionnaireResponse());
            });
            result.put(period, orgCollect);
        });

        List<CaseOrgQuestionnaireResponse> orgQuestionnaireList = listByCaseInstanceId(caseInstanceId);
        if (Objects.isNull(orgQuestionnaireList) || orgQuestionnaireList.isEmpty()) {
            return result.values().stream().toList();
        }

        // questionnaire collect
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> questionnaireCollect = orgQuestionnaireList.stream()
                .collect(Collectors.groupingBy(CaseOrgQuestionnaireResponse::getPeriods, Collectors.toMap(CaseOrgQuestionnaireResponse::getCaseOrgName, v -> v, (v1, v2) -> v1)));
        result.forEach((period, orgMap) -> {
            // 该 period 下的不同机构的问卷
            Map<String, CaseOrgQuestionnaireResponse> orgCollect = questionnaireCollect.get(period);
            orgMap.forEach((org, qn) -> {
                orgMap.replace(org, orgCollect.get(org));
            });
            result.replace(period, orgCollect);
        });

        return result.values().stream().toList();
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 列出已经选择的问卷
     * @date 2023/5/25 11:43
     */
    public Map<String, Map<String, CaseOrgQuestionnaireResponse>> mapSelectedQuestionnaires(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        // 期数 collect
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> result = new LinkedHashMap<>();
        Arrays.stream(CasePeriodsEnum.values()).forEach(item -> {
            result.put(item.getName(), new HashMap<>());
        });

        List<CaseOrgResponse> orgList = listOrgOfCaseInstance(caseInstanceId);
        if (Objects.isNull(orgList) || orgList.isEmpty()) {
            return result;
        }

        // 机构 collect
        result.forEach((period, orgMap) -> {
            Map<String, CaseOrgQuestionnaireResponse> orgCollect = new HashMap<>();
            orgList.forEach(org -> {
                orgCollect.put(org.getOrgName(), new CaseOrgQuestionnaireResponse());
            });
            result.put(period, orgCollect);
        });

        List<CaseOrgQuestionnaireResponse> orgQuestionnaireList = listByCaseInstanceId(caseInstanceId);
        if (Objects.isNull(orgQuestionnaireList) || orgQuestionnaireList.isEmpty()) {
            return result;
        }

        // questionnaire collect
        Map<String, Map<String, CaseOrgQuestionnaireResponse>> questionnaireCollect = orgQuestionnaireList.stream()
                .collect(Collectors.groupingBy(CaseOrgQuestionnaireResponse::getPeriods, Collectors.toMap(CaseOrgQuestionnaireResponse::getCaseOrgName, v -> v, (v1, v2) -> v1)));
        result.forEach((period, orgMap) -> {
            // 该 period 下的不同机构的问卷
            Map<String, CaseOrgQuestionnaireResponse> orgCollect = questionnaireCollect.get(period);
            orgMap.forEach((org, qn) -> {
                orgMap.replace(org, orgCollect.get(org));
            });
            result.replace(period, orgCollect);
        });

        return result;
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description 列出该案例下的问卷
     * @date 2023/5/25 16:36
     */
    public List<CaseOrgQuestionnaireResponse> listByCaseInstanceId(String caseInstanceId) {
        List<CaseOrgQuestionnaireEntity> entityList = listByCaseInstanceId0(caseInstanceId);
        return BeanUtil.copyToList(entityList, CaseOrgQuestionnaireResponse.class);
    }

    /**
     * @param
     * @return
     * @author fhb
     * @description
     * @date 2023/5/25 17:16
     */
    public Boolean saveOrUpdOrgQuestionnaire(List<CaseOrgQuestionnaireRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        List<CaseOrgQuestionnaireEntity> entityList = convertRequest2Entity(requests);
        return orgQuestionnaireService.saveOrUpdateBatch(entityList);
    }

    /**
     * @author fhb
     * @description
     * @date 2023/5/25 17:36
     * @param
     * @return 
     */
    @DSTransactional
    public Boolean autoGenerate(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        // check org
        List<CaseOrgResponse> orgList = listOrgOfCaseInstance(caseInstanceId);
        if (Objects.isNull(orgList) || orgList.isEmpty()) {
            throw  new BizException(CaseESCEnum.CASE_ORG_NON_NULL);
        }

        // check questionnaire of case
        List<CaseQuestionnaireResponse> caseQuestionnaireList = listCaseQuestionnaire(caseInstanceId);
        if (Objects.isNull(caseQuestionnaireList) || caseQuestionnaireList.isEmpty()) {
            throw new BizException(CaseESCEnum.CASE_QUESTIONNAIRE_NON_NULL);
        }

        // clean
        clean(caseInstanceId);

        // save
        List<CaseOrgQuestionnaireRequest> requestList = buildCaseOrgQuestionnaireRequest(caseInstanceId, orgList, caseQuestionnaireList);
        return saveOrUpdOrgQuestionnaire(requestList);
    }

    private void clean(String caseInstanceId) {
        LambdaQueryWrapper<CaseOrgQuestionnaireEntity> queryWrapper = new LambdaQueryWrapper<CaseOrgQuestionnaireEntity>()
                .eq(CaseOrgQuestionnaireEntity::getCaseInstanceId, caseInstanceId);
        orgQuestionnaireService.remove(queryWrapper);
    }

    private List<CaseOrgQuestionnaireRequest> buildCaseOrgQuestionnaireRequest(String caseInstanceId, List<CaseOrgResponse> orgList, List<CaseQuestionnaireResponse> orgQuestionnaireList) {
        Assert.notNull(orgList, CaseESCEnum.CASE_ORG_NON_NULL.getDescr());
        Assert.notNull(orgQuestionnaireList, CaseESCEnum.CASE_QUESTIONNAIRE_NON_NULL.getDescr());

        List<CaseOrgQuestionnaireRequest> result = new ArrayList<>();
        Map<String, List<CaseQuestionnaireResponse>> periodQuestionnaireCollect = orgQuestionnaireList.stream().collect(Collectors.groupingBy(CaseQuestionnaireResponse::getPeriods));
        Arrays.stream(CasePeriodsEnum.values()).forEach(period -> {
            String name = period.getName();
            List<CaseQuestionnaireResponse> questionnaireList = periodQuestionnaireCollect.get(name);
            List<CaseOrgQuestionnaireRequest> request = buildCaseOrgQuestionnaireRequest0(caseInstanceId, name, questionnaireList, orgList);
            result.addAll(request);
        });

        return result;
    }

    private List<CaseOrgQuestionnaireRequest> buildCaseOrgQuestionnaireRequest0(String caseInstanceId, String period, List<CaseQuestionnaireResponse> questionnaireList, List<CaseOrgResponse> orgList) {
        Assert.notNull(orgList, CaseESCEnum.CASE_ORG_NON_NULL.getDescr());
        if (questionnaireList.isEmpty()) {
            return new ArrayList<>();
        }

        LinkedList<CaseQuestionnaireResponse> questionnaireStack = new LinkedList<>();
        questionnaireList.forEach(questionnaireStack::push);

        List<CaseOrgQuestionnaireRequest> result = new ArrayList<>();
        for (CaseOrgResponse org : orgList) {
            CaseQuestionnaireResponse pop = questionnaireStack.pop();
            if (BeanUtil.isEmpty(pop)) {
                break;
            }

            CaseOrgQuestionnaireRequest request = new CaseOrgQuestionnaireRequest();
            request.setCaseQuestionnaireId(baseBiz.getIdStr());
            request.setCaseInstanceId(caseInstanceId);
            request.setCaseOrgId(org.getCaseOrgId());
            request.setCaseQuestionnaireId(pop.getCaseQuestionnaireId());
            request.setPeriods(period);
            result.add(request);
        }

        return result;
    }


    private List<CaseOrgQuestionnaireEntity> convertRequest2Entity(List<CaseOrgQuestionnaireRequest> requests) {
        if (Objects.isNull(requests) || requests.isEmpty()) {
            throw new BizException(CaseESCEnum.PARAMS_NON_NULL);
        }

        List<CaseOrgQuestionnaireEntity> resultList = new ArrayList<>();
        requests.forEach(request -> {
            CaseOrgQuestionnaireEntity result = CaseOrgQuestionnaireEntity.builder()
                    .appId(baseBiz.getAppId())
                    .caseOrgQuestionnaireId(request.getCaseOrgQuestionnaireId())
                    .caseInstanceId(request.getCaseInstanceId())
                    .caseOrgId(request.getCaseOrgId())
                    .caseQuestionnaireId(request.getCaseQuestionnaireId())
                    .periods(request.getPeriods())
                    .build();

            String uniqueId = result.getCaseOrgQuestionnaireId();
            if (StrUtil.isBlank(uniqueId)) {
                result.setCaseOrgQuestionnaireId(baseBiz.getIdStr());
            } else {
                CaseOrgQuestionnaireEntity entity = getById(uniqueId);
                if (BeanUtil.isEmpty(entity)) {
                    throw new BizException(CaseESCEnum.DATA_NULL);
                }
                result.setId(entity.getId());
            }

            resultList.add(result);
        });

        return resultList;
    }

    private CaseOrgQuestionnaireEntity getById(String uniqueId) {
        LambdaQueryWrapper<CaseOrgQuestionnaireEntity> queryWrapper = new LambdaQueryWrapper<CaseOrgQuestionnaireEntity>()
                .eq(CaseOrgQuestionnaireEntity::getCaseOrgQuestionnaireId, uniqueId);
        return orgQuestionnaireService.getOne(queryWrapper);
    }

    private List<CaseOrgResponse> listOrgOfCaseInstance(String caseInstanceId) {
        CaseOrgRequest orgRequest = new CaseOrgRequest();
        orgRequest.setCaseInstanceId(caseInstanceId);
        orgRequest.setPageNo(1);
        orgRequest.setPageSize(10);
        IPage<CaseOrgResponse> orgResponse = orgBiz.listOrgnization(orgRequest);
        return orgResponse.getRecords();
    }

    private List<CaseQuestionnaireResponse> listCaseQuestionnaire(String caseInstanceId) {
        CaseQuestionnaireSearchRequest searchRequest = new CaseQuestionnaireSearchRequest();
        searchRequest.setCaseInstanceId(caseInstanceId);
        return caseQuestionnaireBiz.listCaseQuestionnaire(searchRequest);
    }

    private void filterUnselectedQuestionnaire(String caseInstanceId, List<CaseQuestionnaireResponse> questionnaireList) {
        List<CaseOrgQuestionnaireEntity> orgQuestionnaire = listByCaseInstanceId0(caseInstanceId);
        Map<String, CaseOrgQuestionnaireEntity> collect;
        if (Objects.nonNull(orgQuestionnaire) && !orgQuestionnaire.isEmpty()) {
            collect = orgQuestionnaire.stream().collect(Collectors.toMap(CaseOrgQuestionnaireEntity::getCaseQuestionnaireId, v -> v, (v1, v2) -> v1));
        } else {
            collect = new HashMap<>();
        }

        questionnaireList.removeIf(item -> collect.get(item.getCaseQuestionnaireId()) == null);
    }

    private List<CaseOrgQuestionnaireEntity> listByCaseInstanceId0(String caseInstanceId) {
        return orgQuestionnaireService.lambdaQuery()
                .eq(CaseOrgQuestionnaireEntity::getCaseInstanceId, caseInstanceId)
                .list();
    }
}
