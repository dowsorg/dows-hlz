package org.dows.hep.biz.user.materials;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.account.request.AccountInstanceRequest;
import org.dows.account.request.AccountOrgRequest;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.materials.MaterialsAccessAuthEnum;
import org.dows.hep.api.base.materials.MaterialsESCEnum;
import org.dows.hep.api.base.materials.MaterialsEnabledEnum;
import org.dows.hep.api.base.materials.request.MaterialsPageRequest;
import org.dows.hep.api.base.materials.response.MaterialsPageResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.api.base.person.response.PersonInstanceResponse;
import org.dows.hep.biz.base.materials.MaterialsBaseBiz;
import org.dows.hep.biz.base.materials.MaterialsManageBiz;
import org.dows.hep.biz.base.org.OrgBiz;
import org.dows.hep.biz.base.person.PersonManageBiz;
import org.dows.hep.entity.MaterialsEntity;
import org.dows.hep.service.MaterialsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lait.zhang
 * @description project descr:资料中心:资料信息
 * @date 2023年4月23日 上午9:44:34
 */
@RequiredArgsConstructor
@Service
public class UserMaterialsBiz {
    private final MaterialsBaseBiz baseBiz;
    private final MaterialsService materialsService;
    private final MaterialsManageBiz materialsManageBiz;
    private final PersonManageBiz personManageBiz;
    private final OrgBiz orgBiz;

    /**
     * @param
     * @return
     * @说明: 分页
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public IPage<MaterialsPageResponse> pageMaterials(MaterialsPageRequest request) {
        Page<MaterialsPageResponse> result = new Page<>();
        if (BeanUtil.isEmpty(request)) {
            return result;
        }
        String callAccountId = getTeacherIdOfStudent(request.getAccountId());

        Page<MaterialsEntity> pageRequest = new Page<>(request.getPageNo(), request.getPageSize());
        Page<MaterialsEntity> pageResult = materialsService.lambdaQuery()
                .eq(MaterialsEntity::getAppId, request.getAppId())
                .eq(MaterialsEntity::getBizCode, request.getBizCode())
                .eq(MaterialsEntity::getEnabled, MaterialsEnabledEnum.ENABLED.getCode())
                .and(StrUtil.isNotBlank(callAccountId),
                        i -> i.eq(MaterialsEntity::getAccountId, callAccountId)
                                .or()
                                .eq(MaterialsEntity::getAccessAuth, MaterialsAccessAuthEnum.ACCESS_AUTH_PUBLIC.name()))
                .like(BeanUtil.isNotEmpty(request) && StrUtil.isNotBlank(request.getKeyword()), MaterialsEntity::getTitle, request.getKeyword())
                .orderBy(true, true, MaterialsEntity::getSequence)
                .page(pageRequest);
        result = baseBiz.convertPage(pageResult, MaterialsPageResponse.class);
        fillResult(result);
        return result;
    }

    /**
    * @param
    * @return
     * @说明: 根据ID获取详情
     * @关联表: Materials, MaterialsAttachment
     * @工时: 5H
     * @开发者: fhb
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public MaterialsResponse getMaterials(String materialsId) {
        return materialsManageBiz.getMaterials(materialsId);
    }

    /**
     * Download string.
     *
     * @param materialsId the materials id
     * @return the string
     */
    public String download(String materialsId) {
        return materialsManageBiz.download(materialsId);
    }

    // I am tired, Nasty code
    private String getTeacherIdOfStudent(String accountId) {
        AccountInstanceRequest request = AccountInstanceRequest.builder()
                .accountId(accountId)
                .appId(baseBiz.getAppId())
                .roleName("学生")
                .pageNo(1)
                .pageSize(10)
                .build();
        IPage<AccountInstanceResponse> accountInstanceResponseIPage = personManageBiz.listTeacherOrStudent(request, null);
        List<AccountInstanceResponse> records = accountInstanceResponseIPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return accountId;
        }
        AccountInstanceResponse accountInstanceResponse = records.get(0);
        if (BeanUtil.isEmpty(accountInstanceResponse)) {
            return accountId;
        }
        AccountOrgRequest orgRequest = AccountOrgRequest.builder()
                .appId(baseBiz.getAppId())
                .pageNo(1)
                .pageSize(10)
                .orgId(accountInstanceResponse.getOrgId())
                .build();
        IPage<AccountOrgResponse> accountOrgResponseIPage = orgBiz.listClasss(orgRequest, null);
        List<AccountOrgResponse> records1 = accountOrgResponseIPage.getRecords();
        if (CollUtil.isEmpty(records1)) {
            return accountId;
        }
        AccountOrgResponse accountOrgResponse = records1.get(0);
        if (BeanUtil.isEmpty(accountOrgResponse)) {
            return accountId;
        }
        return accountOrgResponse.getOwnerAccountId();
    }

    public void fillResult(Page<MaterialsPageResponse> result) {
        if (BeanUtil.isEmpty(result)) {
            throw new BizException(MaterialsESCEnum.DATA_NULL);
        }

        List<MaterialsPageResponse> records = result.getRecords();
        if (CollUtil.isEmpty(records)) {
            return;
        }

        Set<String> accountIds = records.stream()
                .map(MaterialsPageResponse::getAccountId)
                .collect(Collectors.toSet());
        AccountInstanceRequest request = AccountInstanceRequest.builder()
                .accountIds(accountIds)
                .appId(baseBiz.getAppId())
                .pageNo(1)
                .pageSize(10)
                .build();
        IPage<PersonInstanceResponse> personInstanceResponseIPage = personManageBiz.listPerson(request);
        Map<String, String> collect = null;
        if (personInstanceResponseIPage != null) {
            List<PersonInstanceResponse> personRecords = personInstanceResponseIPage.getRecords();
            collect = personRecords.stream().collect(Collectors.toMap(PersonInstanceResponse::getAccountId, PersonInstanceResponse::getUserName, (v1, v2) -> v1));
        }

        for (MaterialsPageResponse record: records) {
            Date dt = record.getDt();
            String uploadTime = baseBiz.convertDate2String(dt);
            record.setUploadTime(uploadTime);

            if (collect != null) {
                String userName = collect.get(record.getAccountId());
                record.setUserName(userName);
            }
        }
    }


}