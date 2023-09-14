package org.dows.edw.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @version 1.0
 * @description hep `操作类` 读取biz
 * @date 2023/9/12 17:22
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HepOperateGetRepository {

    private final MongoTemplate mongoTemplate;

    /**
     * @param request - 请求参数
     * @param clazz - 请求实体
     * @return T
     * @author fhb
     * @description 获取唯一性数据
     * @date 2023/9/13 10:41
     */
    public <T> T getOperateEntity(HepOperateGetRequest request, Class<T> clazz) {
        Criteria criteria = Criteria.where("experimentInstanceId").is(request.getExperimentInstanceId())
                .and("experimentGroupId").is(request.getExperimentGroupId())
                .and("operatorId").is(request.getOperatorId())
                .and("orgTreeId").is(request.getOrgTreeId())
                .and("flowId").is(request.getFlowId())
                .and("personId").is(request.getPersonId());
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query, clazz);
    }
}
