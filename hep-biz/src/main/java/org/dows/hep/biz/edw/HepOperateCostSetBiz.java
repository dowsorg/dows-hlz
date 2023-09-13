package org.dows.hep.biz.edw;

import cn.hutool.core.bean.BeanUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.edw.domain.HepOperateCost;
import org.dows.edw.repository.HepFollowUpRespository;
import org.dows.hep.api.edw.request.HepOperateCostGetRequest;
import org.dows.hep.api.edw.request.HepOperateCostSetRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @version 1.0
 * @description hep `操作费用类` 保存biz
 * @date 2023/9/12 15:14
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HepOperateCostSetBiz {

    private final MongoTemplate mongoTemplate;
    private final FieldDefaultValHandler defaultValHandler;

    private final HepOperateCostGetBiz hepOperateCostGetBiz;

    /**
     * @param request - 新增或更新请求参数
     * @return HepOperateCost
     * @author fhb
     * @description 新增或更新
     * @date 2023/9/13 11:26
     */
    public HepOperateCost setOperateEntity(HepOperateCostSetRequest request) {
        HepOperateCostGetRequest hepOperateCostGetRequest = HepOperateCostGetRequest.builder()
                .experimentInstanceId(request.getExperimentInstanceId())
                .experimentGroupId(request.getExperimentGroupId())
                .operatorId(request.getOperatorId())
                .orgTreeId(request.getOrgTreeId())
                .flowId(request.getFlowId())
                .personId(request.getPersonId())
                .build();
        HepOperateCost operateEntity = hepOperateCostGetBiz.getOperateEntity(hepOperateCostGetRequest);

        if (BeanUtil.isNotEmpty(operateEntity)) {
            updOperateEntity(request);
        } else {
            saveOperateEntity(request);
        }

        return hepOperateCostGetBiz.getOperateEntity(hepOperateCostGetRequest);
    }

    /**
     * @param request - 新增的请求参数
     * @return HepOperateCost
     * @author fhb
     * @description 新增
     * @date 2023/9/13 11:28
     */
    public HepOperateCost saveOperateEntity(HepOperateCostSetRequest request) {
        HepOperateCost hepOperateCost = buildEntity(request, true);
        return mongoTemplate.insert(hepOperateCost);
    }

    /**
     * @param request - 更新的请求参数
     * @return com.mongodb.client.result.UpdateResult
     * @author fhb
     * @description 更新
     * @date 2023/9/13 11:29
     */
    HepFollowUpRespository hepFollowUpRespository;
    public UpdateResult updOperateEntity(HepOperateCostSetRequest request) {
        //hepFollowUpRespository.findAll(Example)
        Criteria criteria = Criteria.where("experimentInstanceId").is(request.getExperimentInstanceId())
                .and("experimentGroupId").is(request.getExperimentGroupId())
                .and("operatorId").is(request.getOperatorId())
                .and("orgTreeId").is(request.getOrgTreeId())
                .and("flowId").is(request.getFlowId())
                .and("personId").is(request.getPersonId());
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("feeName", request.getFeeName());
        update.set("feeCode", request.getFeeCode());
        update.set("costType", request.getCostType());
        update.set("cost", request.getCost());
        return mongoTemplate.updateMulti(query, update, HepOperateCost.class);
    }

    private HepOperateCost buildEntity(HepOperateCostSetRequest request, boolean setDefVal) {
        // bean copy
        HepOperateCost hepOperateCost = BeanUtil.copyProperties(request, HepOperateCost.class);
        if (BeanUtil.isEmpty(hepOperateCost)) {
            return hepOperateCost;
        }
        // 是否需要默认值
        if (!setDefVal) {
            return hepOperateCost;
        }

        // 默认值
        defaultValHandler.setDefaultValue(hepOperateCost, HepOperateCost.class);
        return hepOperateCost;
    }
}
