package org.dows.hep.biz.tenant.casus;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.casus.request.CaseNoticeRequest;
import org.dows.hep.api.tenant.casus.response.CaseNoticeResponse;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.entity.CaseNoticeEntity;
import org.dows.hep.service.CaseNoticeService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:案例:案例公告
 * @date 2023年4月17日 下午8:00:11
 */
@RequiredArgsConstructor
@Service
public class TenantCaseNoticeBiz {
    private final TenantCaseBaseBiz baseBiz;
    private final CaseNoticeService caseNoticeService;

    /**
     * @param
     * @return
     * @说明: 新增和更新案例公告
     * @关联表: caseNotice
     * @工时: 8H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月17日 下午8:00:11
     */
    public String saveOrUpdCaseNotice(CaseNoticeRequest caseNotice) {
        if (caseNotice == null) {
            return "";
        }

        if (StrUtil.isBlank(caseNotice.getCaseNoticeId())) {
            caseNotice.setCaseNoticeId(baseBiz.getIdStr());
        }
        CaseNoticeEntity caseNoticeEntity = BeanUtil.copyProperties(caseNotice, CaseNoticeEntity.class);
        caseNoticeService.saveOrUpdate(caseNoticeEntity);

        return caseNoticeEntity.getCaseNoticeId();
    }

    /**
    * @param
    * @return
    * @说明: 列出案例公告
    * @关联表: caseNotice
    * @工时: 5H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public List<CaseNoticeResponse> listCaseNotice(String caseInstanceId) {
        if (StrUtil.isBlank(caseInstanceId)) {
            return new ArrayList<>();
        }

        List<CaseNoticeEntity> entityList = listCaseNotice0(caseInstanceId);
        if (entityList == null || entityList.isEmpty()) {
            return new ArrayList<>();
        }

        return entityList.stream()
                .map(item -> BeanUtil.copyProperties(item, CaseNoticeResponse.class))
                .sorted(Comparator.comparingInt(CaseNoticeResponse::getPeriodSequence))
                .toList();
    }

    public void copyCaseNotice(String oriCaseInstanceId, CaseInstanceEntity targetCaseInstance) {
        List<CaseNoticeEntity> oriEntityList = listCaseNotice0(oriCaseInstanceId);
        if (oriEntityList == null || oriEntityList.isEmpty()) {
            throw new BizException("数据不存在");
        }

        String targetCaseInstanceId = targetCaseInstance.getCaseInstanceId();
        if (StrUtil.isBlank(targetCaseInstanceId)) {
            throw new BizException("数据不存在");
        }

        List<CaseNoticeEntity> targetCaseNoticeList = new ArrayList<>();
        oriEntityList.forEach(item -> {
            CaseNoticeEntity targetCaseNotice = BeanUtil.copyProperties(item, CaseNoticeEntity.class);
            targetCaseNotice.setId(null);
            targetCaseNotice.setCaseNoticeId(baseBiz.getIdStr());
            targetCaseNotice.setCaseInstanceId(targetCaseInstanceId);
            targetCaseNotice.setCaseIdentifier(targetCaseInstance.getCaseIdentifier());
            targetCaseNotice.setVer(targetCaseInstance.getVer());
            targetCaseNoticeList.add(targetCaseNotice);
        });

        caseNoticeService.saveBatch(targetCaseNoticeList);
    }

    /**
    * @param
    * @return
    * @说明: 删除案例公告
    * @关联表: caseNotice
    * @工时: 3H
    * @开发者: fhb
    * @开始时间: 
    * @创建时间: 2023年4月17日 下午8:00:11
    */
    public Boolean delCaseNotice(String caseNoticeId ) {
        if (StrUtil.isBlank(caseNoticeId)) {
            return Boolean.FALSE;
        }

        LambdaQueryWrapper<CaseNoticeEntity> remWrapper = new LambdaQueryWrapper<CaseNoticeEntity>().eq(CaseNoticeEntity::getCaseNoticeId, caseNoticeId);
        return caseNoticeService.remove(remWrapper);
    }

    private List<CaseNoticeEntity> listCaseNotice0(String caseInstanceId) {
        LambdaQueryWrapper<CaseNoticeEntity> queryWrapper = new LambdaQueryWrapper<CaseNoticeEntity>().eq(CaseNoticeEntity::getCaseInstanceId, caseInstanceId);
        return caseNoticeService.list(queryWrapper);
    }


}