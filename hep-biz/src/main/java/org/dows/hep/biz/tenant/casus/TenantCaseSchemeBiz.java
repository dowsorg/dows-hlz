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
import org.dows.hep.biz.base.question.QuestionSectionBiz;
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
    public String saveOrUpdCaseScheme(CaseSchemeRequest caseScheme) {
        if (caseScheme == null) {
            return "";
        }

        // check
        checkBeforeSaveOrUpd(caseScheme);

        // handle
        // save question-section
        QuestionSectionRequest questionSectionRequest = caseScheme2QS(caseScheme);
        String questionSectionId = questionSectionBiz.saveOrUpdQuestionSection(questionSectionRequest);

        // save caseScheme
        CaseSchemeEntity caseSchemeEntity = BeanUtil.copyProperties(caseScheme, CaseSchemeEntity.class);
        caseSchemeEntity.setQuestionSectionId(questionSectionId);
        caseSchemeEntity.setQuestionCount(getQuestionCount(caseScheme));
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
    public Map<String, List<CaseSchemeResponse>> listGroupByCateg(CaseSchemeSearchRequest caseSchemeSearch) {
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

    private QuestionSectionRequest caseScheme2QS(CaseSchemeRequest caseScheme) {
        return QuestionSectionRequest.builder()
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

    private Integer getQuestionCount(CaseSchemeRequest caseScheme) {
        List<QuestionSectionItemRequest> sectionItemList = caseScheme.getSectionItemList();
        return sectionItemList == null ? 0 : sectionItemList.size();
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
                .eq(CaseSchemeEntity::getSource, EnumSource.ADMIN.name());
        List<CaseSchemeEntity> caseSchemeEntityList = caseSchemeService.list(queryWrapper);
        if (caseSchemeEntityList == null || caseSchemeEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        // convert 2 response
        return caseSchemeEntityList.stream()
                .map(item -> BeanUtil.copyProperties(item, CaseSchemeResponse.class))
                .toList();
    }

    private void checkBeforeSaveOrUpd(CaseSchemeRequest caseScheme) {
        String caseSchemeId = caseScheme.getCaseSchemeId();
        if (StrUtil.isBlank(caseSchemeId)) {
            caseScheme.setAppId(baseBiz.getAppId());
            caseScheme.setCaseSchemeId(baseBiz.getIdStr());
            caseScheme.setEnabled(caseScheme.getEnabled() == null ? EnumStatus.ENABLE.getCode() : caseScheme.getEnabled());
            caseScheme.setSource(StrUtil.isBlank(caseScheme.getSource()) ? EnumSource.ADMIN.name() : caseScheme.getSource());
        } else {
            CaseSchemeEntity caseSchemeEntity = getById(caseSchemeId);
            if (BeanUtil.isEmpty(caseSchemeEntity)) {
                throw new BizException("数据不存在");
            }
            caseScheme.setId(caseSchemeEntity.getId());
        }
    }
}