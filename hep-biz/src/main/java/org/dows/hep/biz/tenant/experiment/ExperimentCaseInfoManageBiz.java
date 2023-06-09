package org.dows.hep.biz.tenant.experiment;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.casus.response.CaseInstanceResponse;
import org.dows.hep.api.tenant.casus.response.CaseNoticeResponse;
import org.dows.hep.biz.tenant.casus.TenantCaseManageBiz;
import org.dows.hep.biz.tenant.casus.TenantCaseNoticeBiz;
import org.dows.hep.entity.ExperimentCaseInfoEntity;
import org.dows.hep.service.ExperimentCaseInfoService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExperimentCaseInfoManageBiz {
    private final IdGenerator idGenerator;

    private final TenantCaseManageBiz tenantCaseManageBiz;
    private final TenantCaseNoticeBiz tenantCaseNoticeBiz;

    private final ExperimentCaseInfoService experimentCaseInfoService;

    /**
     * @author fhb
     * @description 预生成社区基本信息-分配实验的时候调用
     * @date 2023/6/1 9:33
     * @param
     * @return
     */
    public void preHandleCaseInfo(String experimentInstanceId, String caseInstanceId) {
        ExperimentCaseInfoEntity entity = ExperimentCaseInfoEntity.builder()
                .experimentCaseInfoId(idGenerator.nextIdStr())
                .experimentInstanceId(experimentInstanceId)
                .build();

        // set case-info
        CaseInstanceResponse caseInstance = tenantCaseManageBiz.getCaseInstance(caseInstanceId);

        // descr
        String descr = Optional.ofNullable(caseInstance)
                .map(CaseInstanceResponse::getDescr)
                .orElse("");
        entity.setDescr(descr);

        // guide
        String guide = Optional.ofNullable(caseInstance)
                .map(CaseInstanceResponse::getGuide)
                .orElse("");
        entity.setGuide(guide);

        // case-notice
        List<CaseNoticeResponse> caseNoticeResponses = tenantCaseNoticeBiz.listCaseNotice(caseInstanceId);
        if (CollUtil.isNotEmpty(caseNoticeResponses)) {
            Map<Integer, Object> noticeMap = new HashMap<>();
            caseNoticeResponses.forEach(caseNotice -> {
                Map<String, String> notice = new HashMap<>();
                notice.put("noticeName", caseNotice.getNoticeName());
                notice.put("noticeContent", caseNotice.getNoticeContent());
                noticeMap.put(caseNotice.getPeriodSequence(), notice);
            });
            String noticeStr = JSON.toJSONString(noticeMap);
            entity.setNotice(noticeStr);
        }

        experimentCaseInfoService.save(entity);
    }
}
