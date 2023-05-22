package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.enums.*;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.tenant.casus.CaseESCEnum;
import org.dows.hep.api.tenant.casus.CaseEnabledEnum;
import org.dows.hep.api.tenant.casus.CaseSchemeSourceEnum;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.service.CaseSchemeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:案例:案例方案设计
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@Service
public class TenantCaseSchemeBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final CaseSchemeService caseSchemeService;
    private final QuestionSectionBiz questionSectionBiz;

    /**
     * @param
     * @return
     * @说明: 新增和更新方案设计
     * @关联表: caseScheme
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    @DSTransactional
    public String saveOrUpdCaseScheme(CaseSchemeRequest caseScheme, CaseSchemeSourceEnum caseSchemeSourceEnum, QuestionSourceEnum questionSourceEnum) {
        if (caseScheme == null) {
            return "";
        }

        CaseSchemeEntity caseSchemeEntity = convertRequest2Entity(caseScheme, caseSchemeSourceEnum, questionSourceEnum);
        caseSchemeService.saveOrUpdate(caseSchemeEntity);

        return caseSchemeEntity.getCaseSchemeId();
    }

    /**
     * @param
     * @return
     * @说明: 分页案例方案
     * @关联表: caseScheme
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public IPage<CaseSchemePageResponse> pageCaseScheme(CaseSchemePageRequest caseSchemePage) {
        if (BeanUtil.isEmpty(caseSchemePage)) {
            return new Page<>();
        }

        // page
        Page<CaseSchemeEntity> page = new Page<>(caseSchemePage.getPageNo(), caseSchemePage.getPageSize());
        Page<CaseSchemeEntity> pageResult = caseSchemeService.lambdaQuery()
                .eq(StrUtil.isNotBlank(caseSchemePage.getCategId()), CaseSchemeEntity::getCaseCategId, caseSchemePage.getCategId())
                .eq(CaseSchemeEntity::getSource, CaseSchemeSourceEnum.ADMIN.name())
                .like(StrUtil.isNotBlank(caseSchemePage.getKeyword()), CaseSchemeEntity::getSchemeName, caseSchemePage.getKeyword())
                .page(page);

        // convert
        return baseBiz.convertPage(pageResult, CaseSchemePageResponse.class);
    }

    /**
     * @param
     * @return
     * @说明:
     * @关联表: caseScheme
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public List<CaseSchemeResponse> listCaseSchemeOfDS(CaseSchemeSearchRequest caseSchemeSearch) {
        return list(caseSchemeSearch);
    }

    /**
     * @param
     * @return
     * @说明:
     * @关联表: caseScheme
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年5月6日 下午14:00:11
     */
    public Map<String, List<CaseSchemeResponse>> listSchemeGroupOfDS(CaseSchemeSearchRequest caseSchemeSearch) {
        List<CaseSchemeResponse> responseList = list(caseSchemeSearch);
        if(responseList == null || responseList.isEmpty()) {
            return new HashMap<>();
        }

        // group by categ
        return responseList.stream().collect(Collectors.groupingBy(CaseSchemeResponse::getCaseCategName));
    }

    /**
     * @param
     * @return
     * @说明: 获取案例方案
     * @关联表: caseScheme
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public CaseSchemeResponse getCaseScheme(String caseSchemeId) {
        CaseSchemeEntity caseSchemeEntity = getById(caseSchemeId);
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);
        // set question-section
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        setQuestionSection(questionSectionId, result);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 根据 caseInstanceId 获取案例方案
     * @关联表: caseScheme
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public CaseSchemeResponse getCaseSchemeByInstanceId(String caseInstanceId) {
        CaseSchemeEntity caseSchemeEntity = getByInstanceId(caseInstanceId);
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);
        // set question-section
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        setQuestionSection(questionSectionId, result);
        return result;
    }

    /**
     * @param
     * @return
     * @说明: 启用案例方案
     * @关联表: caseScheme
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
    */
    public Boolean enabledCaseScheme(String caseSchemeId ) {
        return changeStatus(caseSchemeId, CaseEnabledEnum.ENABLED);
    }

    /**
    * @param
    * @return
    * @说明: 禁用案例方案
    * @关联表: caseScheme
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public Boolean disabledCaseScheme(String caseSchemeId ) {
        return changeStatus(caseSchemeId, CaseEnabledEnum.DISABLED);
    }

    /**
     * @param
     * @return
     * @说明: 复制案例方案
     * @关联表: caseScheme
     * @工时: 3H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public void copyCaseScheme(String oriCaseInstanceId, CaseInstanceEntity targetCaseInstance) {
        CaseSchemeEntity oriEntity = getByInstanceId(oriCaseInstanceId);
        if (BeanUtil.isEmpty(oriEntity)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }

        String targetCaseInstanceId = targetCaseInstance.getCaseInstanceId();
        if (StrUtil.isBlank(targetCaseInstanceId)) {
            throw new BizException(CaseESCEnum.DATA_NULL);
        }

        CaseSchemeEntity targetCaseScheme = BeanUtil.copyProperties(oriEntity, CaseSchemeEntity.class);
        targetCaseScheme.setId(null);
        targetCaseScheme.setCaseSchemeId(baseBiz.getIdStr());
        targetCaseScheme.setCaseInstanceId(targetCaseInstanceId);
        caseSchemeService.save(targetCaseScheme);
    }

    /**
     * @param
     * @return
     * @说明: 删除or批量删除案例方案
     * @关联表: caseScheme
     * @工时: 6H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public Boolean delCaseScheme(List<String> caseSchemeIds) {
        if (caseSchemeIds == null || caseSchemeIds.isEmpty()) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .in(CaseSchemeEntity::getCaseSchemeId, caseSchemeIds);
        return caseSchemeService.remove(queryWrapper);
    }

    private boolean changeStatus(String caseSchemeId, CaseEnabledEnum enumStatus) {
        LambdaUpdateWrapper<CaseSchemeEntity> updateWrapper = new LambdaUpdateWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId)
                .set(CaseSchemeEntity::getEnabled, enumStatus.getCode());
        return caseSchemeService.update(updateWrapper);
    }

    private void setQuestionSection(String questionSectionId, CaseSchemeResponse result) {
        // get and set question-section
        QuestionSectionResponse questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId);
        if (BeanUtil.isEmpty(questionSectionResponse)) {
            return;
        }
        List<QuestionSectionItemResponse> sectionItemList = questionSectionResponse.getSectionItemList();
        List<QuestionSectionDimensionResponse> questionSectionDimensionList = questionSectionResponse.getQuestionSectionDimensionList();
        result.setSectionItemList(sectionItemList);
        result.setQuestionSectionDimensionList(questionSectionDimensionList);
    }

    private CaseSchemeEntity getById(String caseSchemeId) {
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId);
        return caseSchemeService.getOne(queryWrapper);
    }

    private List<CaseSchemeResponse> list(CaseSchemeSearchRequest caseSchemeSearch) {
        if (caseSchemeSearch == null) {
            return new ArrayList<>();
        }

        // list admin
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(StrUtil.isNotBlank(caseSchemeSearch.getSource()), CaseSchemeEntity::getSource, caseSchemeSearch.getSource())
                .eq(StrUtil.isNotBlank(caseSchemeSearch.getCaseInstanceId()), CaseSchemeEntity::getCaseInstanceId, caseSchemeSearch.getCaseInstanceId())
                .eq(StrUtil.isNotBlank(caseSchemeSearch.getAppId()), CaseSchemeEntity::getAppId, caseSchemeSearch.getAppId())
                .in(caseSchemeSearch.getCategIds() != null, CaseSchemeEntity::getCaseCategId, caseSchemeSearch.getCategIds())
                .like(StrUtil.isNotBlank(caseSchemeSearch.getKeyword()), CaseSchemeEntity::getSchemeName, caseSchemeSearch.getKeyword());
        List<CaseSchemeEntity> caseSchemeEntityList = caseSchemeService.list(queryWrapper);
        if (caseSchemeEntityList == null || caseSchemeEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        // convert 2 response
        return caseSchemeEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, CaseSchemeResponse.class))
                .toList();
    }

    private CaseSchemeEntity convertRequest2Entity(CaseSchemeRequest request, CaseSchemeSourceEnum caseSchemeSourceEnum, QuestionSourceEnum questionSourceEnum) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(QuestionESCEnum.PARAMS_NON_NULL);
        }

        CaseSchemeEntity result = CaseSchemeEntity.builder()
                .appId(baseBiz.getAppId())
                .caseSchemeId(request.getCaseSchemeId())
                .caseInstanceId(request.getCaseInstanceId())
                .schemeName(request.getSchemeName())
                .caseCategId(request.getCaseCategId())
                .enabled(request.getEnabled())
                .tips(request.getTips())
                .schemeDescr(request.getSchemeDescr())
                .addType(request.getAddType())
                .containsVideo(request.getContainsVideo())
                .videoQuestion(request.getVideoQuestion())
                .source(caseSchemeSourceEnum.name())
                .accountId(request.getAccountId())
                .accountName(request.getAccountName())
                .build();

        String caseSchemeId = result.getCaseSchemeId();
        if (StrUtil.isBlank(caseSchemeId)) {
            result.setCaseSchemeId(baseBiz.getIdStr());
        } else {
            CaseSchemeEntity oriEntity = getById(caseSchemeId);
            if (BeanUtil.isEmpty(oriEntity)) {
                throw new BizException(CaseESCEnum.DATA_NULL);
            }
            result.setId(oriEntity.getId());
        }

        String questionSectionId = saveOrUpdQuestionSection(request, questionSourceEnum);
        result.setQuestionSectionId(questionSectionId);
        int questionCount = getQuestionCount(request);
        if (result.getContainsVideo() != null && result.getContainsVideo() == 1) {
            questionCount += 1;
        }
        result.setQuestionCount(questionCount);
        return result;
    }

    private String saveOrUpdQuestionSection(CaseSchemeRequest caseScheme, QuestionSourceEnum questionSourceEnum) {
        QuestionSectionRequest questionSectionRequest = caseScheme2QS(caseScheme);
        return questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest, QuestionSectionAccessAuthEnum.PRIVATE_VIEWING, questionSourceEnum);
    }

    private QuestionSectionRequest caseScheme2QS(CaseSchemeRequest caseScheme) {
        return QuestionSectionRequest.builder()
                .name(caseScheme.getSchemeName())
                .tips(caseScheme.getTips())
                .descr(caseScheme.getSchemeDescr())
                .enabled(QuestionEnabledEnum.ENABLED.getCode())
                .accountId(caseScheme.getAccountId())
                .accountName(caseScheme.getAccountName())
                .sectionItemList(caseScheme.getSectionItemList())
                .questionSectionDimensionList(caseScheme.getQuestionSectionDimensionList())
                .generationMode(QuestionSectionGenerationModeEnum.ADD_NEW)
                .build();
    }

    private Integer getQuestionCount(CaseSchemeRequest caseScheme) {
        List<QuestionSectionItemRequest> sectionItemList = caseScheme.getSectionItemList();
        return sectionItemList == null ? 0 : sectionItemList.size();
    }

    private CaseSchemeEntity getByInstanceId(String caseInstanceId) {
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseInstanceId, caseInstanceId);
        return caseSchemeService.getOne(queryWrapper);
    }
}