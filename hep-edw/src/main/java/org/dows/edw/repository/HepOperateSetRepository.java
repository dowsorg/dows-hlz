package org.dows.edw.repository;

import cn.hutool.core.bean.BeanUtil;
import com.mongodb.client.result.UpdateResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.edw.HepOperateEntity;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * @author fhb
 * @version 1.0
 * @description hep `操作类` 保存biz
 * @date 2023/9/12 15:13
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class HepOperateSetRepository {

    private final MongoTemplate mongoTemplate;
    private final FieldDefaultValHandler defaultValHandler;

    private final HepOperateGetRepository hepOperateGetRepository;

    /**
     * @param request - 新增或更新请求参数
     * @param clazz   - 新增或更新对象的Class
     * @return T
     * @author fhb
     * @description 新增或更新
     * @date 2023/9/13 11:26
     */
    public <T extends HepOperateEntity> T setOperateEntity(HepOperateSetRequest request, Class<T> clazz) {
        HepOperateGetRequest hepOperateGetRequest = HepOperateGetRequest.builder()
                .type(request.getType())
                .experimentInstanceId(request.getExperimentInstanceId())
                .experimentGroupId(request.getExperimentGroupId())
                .operatorId(request.getOperatorId())
                .orgTreeId(request.getOrgTreeId())
                .flowId(request.getFlowId())
                .personId(request.getPersonId())
                .build();
        T operateEntity = hepOperateGetRepository.getOperateEntity(hepOperateGetRequest, clazz);

        if (BeanUtil.isNotEmpty(operateEntity)) {
            updOperateEntity(request, clazz);
        } else {
            saveOperateEntity(request, clazz);
        }

        return hepOperateGetRepository.getOperateEntity(hepOperateGetRequest, clazz);
    }

    /**
     * @param request - 新增的请求参数
     * @param clazz   - 新增对象的Class
     * @return T
     * @author fhb
     * @description 新增
     * @date 2023/9/13 11:28
     */
    public <T> T saveOperateEntity(HepOperateSetRequest request, Class<T> clazz) {
        T t = buildEntity(request, clazz, true);
        return mongoTemplate.insert(t);
    }

    /**
     * @param request - 更新的请求参数
     * @param clazz   - 更新对象的Class
     * @return com.mongodb.client.result.UpdateResult
     * @author fhb
     * @description 更新
     * @date 2023/9/13 11:29
     */
    public <T> UpdateResult updOperateEntity(HepOperateSetRequest request, Class<T> clazz) {
        Criteria criteria = Criteria.where("experimentInstanceId").is(request.getExperimentInstanceId())
                .and("experimentGroupId").is(request.getExperimentGroupId())
                .and("operatorId").is(request.getOperatorId())
                .and("orgTreeId").is(request.getOrgTreeId())
                .and("flowId").is(request.getFlowId())
                .and("personId").is(request.getPersonId());
        Query query = new Query(criteria);
        Update update = new Update();
        update.set("data", request.getData());
        return mongoTemplate.updateMulti(query, update, clazz);
    }

    private <T> T buildEntity(HepOperateSetRequest request, Class<T> clazz, boolean setDefVal) {
        // bean copy
        T t = BeanUtil.copyProperties(request, clazz);
        if (BeanUtil.isEmpty(t)) {
            return t;
        }
        // 是否需要默认值
        if (!setDefVal) {
            return t;
        }

        // 默认值
        defaultValHandler.setDefaultValue(t, clazz);
        return t;
    }
}
