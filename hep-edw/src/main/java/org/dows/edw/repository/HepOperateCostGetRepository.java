package org.dows.edw.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.edw.domain.HepOperateCost;
import org.dows.hep.api.edw.request.HepOperateCostGetRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @version 1.0
 * @description hep `操作费用类` 读取biz
 * @date 2023/9/12 17:50
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HepOperateCostGetRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * @param request - 请求参数
     * @return HepOperateCost
     * @author fhb
     * @description 获取唯一性数据
     * @date 2023/9/13 10:41
     */
    public HepOperateCost getOperateEntity(HepOperateCostGetRequest request) {
        Criteria criteria = Criteria.where("experimentInstanceId").is(request.getExperimentInstanceId())
                .and("experimentGroupId").is(request.getExperimentGroupId())
                .and("operatorId").is(request.getOperatorId())
                .and("orgTreeId").is(request.getOrgTreeId())
                .and("flowId").is(request.getFlowId())
                .and("personId").is(request.getPersonId());
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, HepOperateCost.class);
    }
}
