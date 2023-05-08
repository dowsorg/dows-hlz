package org.dows.hep.biz.user.person;

import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountOrgGeoApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgGeoResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.entity.CaseOrgEntity;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.service.CaseOrgService;
import org.dows.hep.service.CasePersonService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author jx
 * @date 2023/5/8 13:37
 */
@Service
@RequiredArgsConstructor
public class PersonStatiscBiz {

    private final CaseOrgService caseOrgService;

    private final CasePersonService casePersonService;

    private final AccountInstanceApi accountInstanceApi;

    private final AccountOrgGeoApi accountOrgGeoApi;

    /**
     * @param
     * @return
     * @说明: 获取社区人数
     * @关联表: case_person、case_org
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public Integer countCasePersons(String caseInstanceId) {
        Integer count = 0;
        //1、获取案例中的已经被开启的机构
        List<CaseOrgEntity> orgList = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseInstanceId, caseInstanceId)
                .eq(CaseOrgEntity::getDeleted, false)
                .list();
        //2、遍历机构，获取每个机构中已经开启的案例人物
        if (orgList != null && orgList.size() > 0) {
            for (CaseOrgEntity org : orgList) {
                List<CasePersonEntity> personList = casePersonService.lambdaQuery()
                        .eq(CasePersonEntity::getCaseOrgId, org.getCaseOrgId())
                        .eq(CasePersonEntity::getDeleted, false)
                        .list();
                if (personList != null && personList.size() > 0) {
                    for (CasePersonEntity person : personList) {
                        AccountInstanceResponse instance = accountInstanceApi.getAccountInstanceByAccountId(person.getAccountId());
                        if (instance != null && !ReflectUtil.isObjectNull(instance)) {
                            if (instance.getStatus() == 1) {
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 获取案例机构
     * @关联表: case_person、case_org
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public List<AccountOrgResponse> countCaseOrgs(String caseInstanceId) {
        //1、获取案例中的已经被开启的机构
        List<CaseOrgEntity> orgList = caseOrgService.lambdaQuery()
                .eq(CaseOrgEntity::getCaseInstanceId, caseInstanceId)
                .eq(CaseOrgEntity::getDeleted, false)
                .list();
        //2、获取机构的经纬度信息
        List<AccountOrgResponse> orgResponses = new ArrayList<>();
        if(orgList != null && orgList.size() > 0){
            orgList.forEach(org->{
                AccountOrgResponse orgResponse = AccountOrgResponse
                        .builder()
                        .orgId(org.getCaseOrgId())
                        .orgName(org.getOrgName())
                        .build();
                AccountOrgGeoResponse orgGeo = accountOrgGeoApi.getAccountOrgInfoByOrgId(org.getOrgId());
                orgResponse.setOrgLatitude(orgGeo.getOrgLatitude());
                orgResponse.setOrgLongitude(orgGeo.getOrgLongitude());
                orgResponses.add(orgResponse);
            });
        }
        return orgResponses;
    }
}
