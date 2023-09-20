package org.dows.hep.biz.user.organization;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.account.api.AccountGroupApi;
import org.dows.account.api.AccountOrgApi;
import org.dows.account.request.AccountGroupRequest;
import org.dows.account.response.AccountGroupResponse;
import org.dows.account.response.AccountOrgResponse;
import org.dows.hep.api.user.organization.request.CaseOrgFeeRequest;
import org.dows.hep.api.user.organization.request.OrgPositionRequest;
import org.dows.hep.api.user.organization.request.PersonQueryRequest;
import org.dows.hep.api.user.organization.request.TransferPersonelRequest;
import org.dows.hep.api.user.organization.response.AccountOrgGeoResponse;
import org.dows.hep.api.user.organization.response.OrganizationFunsResponse;
import org.dows.hep.api.user.organization.response.PersonInstanceResponse;
import org.dows.hep.biz.eval.ExperimentPersonCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.OperateTransfersEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.hep.service.OperateTransfersService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:机构:机构操作
 * @date 2023年4月23日 上午9:44:34
 */
@Service
@RequiredArgsConstructor
public class HepOrgOperateBiz {

    private final ExperimentPersonService experimentPersonService;

    private final AccountGroupApi accountGroupApi;

    private final AccountOrgApi accountOrgApi;

    private final OperateTransfersService operateTransfersService;

    private final IdGenerator idGenerator;

    /**
     * @param
     * @return
     * @说明: 获取机构组员列表[人物档案]
     * @关联表: AccountGroup、AccountUser、UserInstance、IndicatorInstance、IndicatorPrincipalRef、ExperimentPerson，标签表？？
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public PersonInstanceResponse listPerson(PersonQueryRequest personQuery) {
        return new PersonInstanceResponse();
    }

    /**
     * @param
     * @return
     * @说明: 列出机构功能
     * @关联表: CaseOrgFunction
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public List<OrganizationFunsResponse> listOrgFunc(String orgId) {
        return new ArrayList<OrganizationFunsResponse>();
    }

    /**
     * @param
     * @return
     * @说明: 列出机构费用
     * @关联表: caseOrgFee
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public Boolean listOrgFee(CaseOrgFeeRequest caseOrgFee) {
        return Boolean.FALSE;
    }

    /**
     * @param
     * @return
     * @说明: 转移人员
     * @关联表: operate_transfers, account_group,experiment_person
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月09日 下午14:41:34
     */
    @DSTransactional
    public Boolean transferPerson(TransferPersonelRequest request,
                                  String operateAccountId,
                                  String operateAccountName,
                                  Integer periods
                                  ) {
        Boolean flag = false;
        //1、通过实验账户ID找到用户ID
        ExperimentPersonEntity experimentPerson = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentPersonId,request.getAccountId())
                .eq(ExperimentPersonEntity::getExperimentInstanceId,request.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getExperimentGroupId,request.getExperimentGroupId())
                .eq(ExperimentPersonEntity::getDeleted,false)
                .one();
        //2、根据账户找到对应的机构
        List<AccountGroupResponse> groupResponseList = accountGroupApi.getAccountGroupListByAccountId(experimentPerson.getAccountId(), "3");
        //3、删除该条数据，并将账户添加到新机构
        if (groupResponseList != null && groupResponseList.size() > 0) {
            AccountOrgResponse orgResponse = accountOrgApi.getAccountOrgByOrgId(request.getOrgId(), "3");
            groupResponseList.forEach(group -> {
                AccountGroupRequest request1 = new AccountGroupRequest();
                BeanUtil.copyProperties(orgResponse, request1);
                request1.setOrgId(request.getOrgId());
                request1.setOrgName(orgResponse.getOrgName());
                request1.setAccountId(group.getAccountId());
                request1.setAccountName(group.getAccountName());
                request1.setUserId(group.getUserId());
                request1.setId(group.getId());
                accountGroupApi.updateOrgById(request1);
            });
            //4、添加转移记录信息
            OperateTransfersEntity operateTransfersEntity = OperateTransfersEntity.builder()
                    .operateTransfersId(idGenerator.nextIdStr())
                    .experimentInstanceId(request.getExperimentInstanceId())
                    .experimentGroupId(request.getExperimentGroupId())
                    .experimentPersonId(request.getAccountId())
                    .formOrgId(groupResponseList.get(0).getOrgId())
                    .formOrgName(groupResponseList.get(0).getOrgName())
                    .toOrgId(request.getOrgId())
                    .toOrgName(orgResponse.getOrgName())
                    .experimentAccountName(experimentPerson.getAccountName())
                    .operateAccountId(operateAccountId)
                    .operateAccountName(operateAccountName)
                    .descr(request.getDescr())
                    .periods(periods)
                    .build();
            flag = operateTransfersService.save(operateTransfersEntity);
            //5、更改实验账户所属机构
            LambdaUpdateWrapper<ExperimentPersonEntity> updateWrapper = new LambdaUpdateWrapper<ExperimentPersonEntity>()
                    .eq(ExperimentPersonEntity::getId, experimentPerson.getId())
                    .eq(ExperimentPersonEntity::getExperimentPersonId, request.getAccountId())
                    .eq(ExperimentPersonEntity::getExperimentInstanceId, request.getExperimentInstanceId())
                    .eq(ExperimentPersonEntity::getExperimentGroupId, request.getExperimentGroupId())
                    .set(ExperimentPersonEntity::getExperimentOrgId, request.getExperimentOrgId())
                    .set(ExperimentPersonEntity::getExperimentOrgName, orgResponse.getOrgName());
            AssertUtil.falseThenThrow(experimentPersonService.update(updateWrapper))
                    .throwMessage("系统繁忙，请稍后重试");
            final ExperimentCacheKey cacheKey = ExperimentCacheKey.create(experimentPerson.getAppId(), experimentPerson.getExperimentInstanceId());
            PersonIndicatorIdCache.Instance().loadingCache().invalidate(experimentPerson.getExperimentPersonId());
            ExperimentPersonCache.Instance().remove(request.getExperimentInstanceId());
            PersonIndicatorIdCache.Instance().getSet(experimentPerson.getExperimentPersonId(), false);
            ExperimentPersonCache.Instance().getSet(cacheKey, false);
        }
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 列出机构位置
     * @关联表: AccountOrgGeo
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public AccountOrgGeoResponse listOrgPosition(OrgPositionRequest orgPosition) {
        return new AccountOrgGeoResponse();
    }
}