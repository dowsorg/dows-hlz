package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;
import org.dows.hep.api.base.question.request.QuestionSectionRequest;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;
import org.dows.hep.api.base.question.response.QuestionSectionResponse;
import org.dows.hep.api.enums.EnumSource;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.api.tenant.casus.request.CaseSchemePageRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeRequest;
import org.dows.hep.api.tenant.casus.request.CaseSchemeSearchRequest;
import org.dows.hep.api.tenant.casus.response.CaseSchemePageResponse;
import org.dows.hep.api.tenant.casus.response.CaseSchemeResponse;
import org.dows.hep.biz.base.question.BaseBiz;
import org.dows.hep.biz.base.question.QuestionSectionBiz;
import org.dows.hep.entity.CaseSchemeEntity;
import org.dows.hep.service.CaseSchemeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:案例:案例方案设计
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@Service
public class TenantCaseSchemeBiz {
    private final BaseBiz baseBiz;
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
    @Transactional
    public String saveCaseScheme(CaseSchemeRequest caseScheme) {
        // base Info
        caseScheme.setAppId(baseBiz.getAppId());
        caseScheme.setCaseSchemeId(baseBiz.getIdStr());
        caseScheme.setEnabled(caseScheme.getEnabled() == null ? EnumStatus.ENABLE.getCode() : caseScheme.getEnabled());
        caseScheme.setSource(StrUtil.isBlank(caseScheme.getSource()) ? EnumSource.ADMIN.name() : caseScheme.getSource());
        List<QuestionSectionItemRequest> sectionItemList = caseScheme.getSectionItemList();
        Integer questionCount = sectionItemList == null ? 0 : sectionItemList.size();
        // TODO categIdPath...

        // save question section
        String questionSectionId = saveQuestionSection(caseScheme);

        // save caseScheme
        CaseSchemeEntity caseSchemeEntity = BeanUtil.copyProperties(caseScheme, CaseSchemeEntity.class);
        caseSchemeEntity.setQuestionSectionId(questionSectionId);
        caseSchemeEntity.setQuestionCount(questionCount);
        caseSchemeService.save(caseSchemeEntity);
        return caseSchemeEntity.getCaseSchemeId();
    }

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
    public Boolean updCaseScheme(CaseSchemeRequest caseScheme) {
        CaseSchemeEntity caseSchemeEntity = BeanUtil.copyProperties(caseScheme, CaseSchemeEntity.class);
        LambdaUpdateWrapper<CaseSchemeEntity> updateWrapper = new LambdaUpdateWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseScheme.getCaseSchemeId());
        return caseSchemeService.update(caseSchemeEntity, updateWrapper);
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
    public Page<CaseSchemePageResponse> pageCaseScheme(CaseSchemePageRequest caseSchemePage) {
        if (BeanUtil.isEmpty(caseSchemePage)) {
            return new Page<>();
        }
        Page<CaseSchemePageResponse> result = new Page<>();

        // page
        Page<CaseSchemeEntity> page = new Page<>(caseSchemePage.getPageNo(), caseSchemePage.getPageSize());
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(caseSchemePage.getCategId() != null, CaseSchemeEntity::getCaseCategId, caseSchemePage.getCategId())
                .eq(CaseSchemeEntity::getSource, EnumSource.ADMIN.name())
                .like(caseSchemePage.getKeyword() != null, CaseSchemeEntity::getSchemeName, caseSchemePage.getKeyword())
                .like(caseSchemePage.getKeyword() != null, CaseSchemeEntity::getAccountName, caseSchemePage.getKeyword());
        Page<CaseSchemeEntity> pageResult = caseSchemeService.page(page, queryWrapper);

        // convert
        List<CaseSchemeEntity> records = pageResult.getRecords();
        if (records == null || records.isEmpty()) {
            return result;
        }
        List<CaseSchemePageResponse> pageResponseList = records.stream()
                .map(item -> BeanUtil.copyProperties(item, CaseSchemePageResponse.class))
                .collect(Collectors.toList());
        result.setRecords(pageResponseList);
        return result;
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
    public List<CaseSchemeResponse> listCaseScheme(CaseSchemeSearchRequest caseSchemeSearch) {
        return new ArrayList<CaseSchemeResponse>();
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
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId);
        CaseSchemeEntity caseSchemeEntity = caseSchemeService.getOne(queryWrapper);
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);
        // set question-section
        setQuestionSection(caseSchemeEntity, result);
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
        LambdaQueryWrapper<CaseSchemeEntity> queryWrapper = new LambdaQueryWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseInstanceId, caseInstanceId);
        CaseSchemeEntity caseSchemeEntity = caseSchemeService.getOne(queryWrapper);
        CaseSchemeResponse result = BeanUtil.copyProperties(caseSchemeEntity, CaseSchemeResponse.class);
        // set question-section
        setQuestionSection(caseSchemeEntity, result);
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
        return changeStatus(caseSchemeId, EnumStatus.ENABLE);
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
        return changeStatus(caseSchemeId, EnumStatus.DISABLE);
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

    private String saveQuestionSection(CaseSchemeRequest caseScheme) {
        QuestionSectionRequest questionSectionRequest = QuestionSectionRequest.builder()
                .name(caseScheme.getSchemeName())
                .tips(caseScheme.getTips())
                .descr(caseScheme.getSchemeDescr())
                .enabled(EnumStatus.ENABLE.getCode())
                .accountId(caseScheme.getAccountId())
                .accountName(caseScheme.getAccountName())
                .sectionItemList(caseScheme.getSectionItemList())
                .questionSectionDimensionList(caseScheme.getQuestionSectionDimensionList())
                .appId(caseScheme.getAppId())
                .source(caseScheme.getSource())
                .build();
        return questionSectionBiz.saveQuestionSection(questionSectionRequest);
    }

    private boolean changeStatus(String caseSchemeId, EnumStatus enumStatus) {
        LambdaUpdateWrapper<CaseSchemeEntity> updateWrapper = new LambdaUpdateWrapper<CaseSchemeEntity>()
                .eq(CaseSchemeEntity::getCaseSchemeId, caseSchemeId)
                .set(CaseSchemeEntity::getEnabled, enumStatus.getCode());
        return caseSchemeService.update(updateWrapper);
    }

    private void setQuestionSection(CaseSchemeEntity caseSchemeEntity, CaseSchemeResponse result) {
        // get and set question-section
        String questionSectionId = caseSchemeEntity.getQuestionSectionId();
        QuestionSectionResponse questionSectionResponse = questionSectionBiz.getQuestionSection(questionSectionId);
        if (BeanUtil.isEmpty(questionSectionResponse)) {
            return;
        }
        List<QuestionSectionItemResponse> sectionItemList = questionSectionResponse.getSectionItemList();
        List<QuestionSectionDimensionResponse> questionSectionDimensionList = questionSectionResponse.getQuestionSectionDimensionList();
        result.setSectionItemList(sectionItemList);
        result.setQuestionSectionDimensionList(questionSectionDimensionList);
    }
}