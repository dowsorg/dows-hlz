package org.dows.hep.biz.user.person;

import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountInstanceApi;
import org.dows.account.api.AccountOrgGeoApi;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgGeoResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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

    private final ExperimentParticipatorService experimentParticipatorService;

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentOrgService experimentOrgService;

    /**
     * @param
     * @return
     * @说明: 获取社区人数
     * @关联表: experiment_person、experiment_org
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public Integer countExperimentPersons(String experimentInstanceId) {
        Integer count = 0;
        //1、获取案例中的已经被开启的机构
        List<ExperimentOrgEntity> experimentOrgList = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        if (experimentOrgList != null && experimentOrgList.size() > 0) {
            List<CaseOrgEntity> orgList = caseOrgService.lambdaQuery()
                    .eq(CaseOrgEntity::getCaseOrgId, experimentOrgList.get(0).getCaseOrgId())
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
        }
        return count;
    }

    /**
     * @param
     * @return
     * @说明: 获取实验机构
     * @关联表: experiment_org、account_org_geo
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 上午13:57:34
     */
    public List<AccountOrgResponse> listExperimentOrgs(String experimentInstanceId, String experimentGroupId) {
        List<AccountOrgResponse> orgResponses = new ArrayList<>();
        //1、根据实验找到案例机构ID
        List<ExperimentOrgEntity> experimentOrgList = experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentOrgEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentOrgEntity::getDeleted, false)
                .list();
        if (experimentOrgList != null && experimentOrgList.size() > 0) {
            //2、获取机构的经纬度信息
            experimentOrgList.forEach(org -> {
                AccountOrgResponse orgResponse = AccountOrgResponse
                        .builder()
                        .experimentOrgId(org.getExperimentOrgId())
                        .orgId(org.getOrgId())
                        .build();
                AccountOrgGeoResponse orgGeo = accountOrgGeoApi.getAccountOrgInfoByOrgId(org.getOrgId());
                orgResponse.setOrgLatitude(orgGeo.getOrgLatitude());
                orgResponse.setOrgLongitude(orgGeo.getOrgLongitude());
                orgResponse.setOrgName(orgGeo.getOrgName());
                orgResponses.add(orgResponse);
            });
        }
        return orgResponses;
    }

    /**
     * @param
     * @return
     * @说明: 获取参与者信息
     * @关联表: experiment_participator、account_instance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月8日 下午16:35:34
     */
    public ExperimentParticipatorResponse getParticipatorInfo(String experimentParticipatorId) {
        ExperimentParticipatorResponse experimentParticipatorResponse = new ExperimentParticipatorResponse();
        ExperimentParticipatorEntity participatorEntity = experimentParticipatorService
                .lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentParticipatorId, experimentParticipatorId)
                .eq(ExperimentParticipatorEntity::getDeleted, false)
                .one();
        //1、获取参与者小组信息
        if (participatorEntity != null && !ReflectUtil.isObjectNull(participatorEntity)) {
            experimentParticipatorResponse.setAccountId(participatorEntity.getAccountId());
            experimentParticipatorResponse.setAccountName(participatorEntity.getAccountName());
            ExperimentGroupEntity groupEntity = experimentGroupService
                    .lambdaQuery()
                    .eq(ExperimentGroupEntity::getDeleted, false)
                    .eq(ExperimentGroupEntity::getExperimentGroupId, participatorEntity.getExperimentGroupId())
                    .one();
            if (groupEntity != null && !ReflectUtil.isObjectNull(groupEntity)) {
                experimentParticipatorResponse.setGroupName(groupEntity.getGroupName());
            }
        }
        //2、获取参与者负责的uim机构
        List<String> experimentOrgIds = Arrays.asList(participatorEntity.getExperimentOrgIds());
        List<String> orgIds = new ArrayList<>();
        if (experimentOrgIds != null && experimentOrgIds.size() > 0) {
            experimentOrgIds.forEach(experimentOrgId -> {
                ExperimentOrgEntity orgEntity = experimentOrgService.lambdaQuery()
                        .eq(ExperimentOrgEntity::getExperimentOrgId, experimentOrgId)
                        .eq(ExperimentOrgEntity::getDeleted, false)
                        .one();
                if(orgEntity != null){
                    orgIds.add(orgEntity.getOrgId());
                }
            });
        }
        experimentParticipatorResponse.setOrgIds(orgIds);
        //3、根据账号ID找到头像
        AccountInstanceResponse instanceResponse = accountInstanceApi.getAccountInstanceByAccountId(participatorEntity.getAccountId());
        experimentParticipatorResponse.setAvatar(instanceResponse.getAvatar());
        return experimentParticipatorResponse;
    }
}
