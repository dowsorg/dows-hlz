package org.dows.hep.biz.base.risk;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.risk.request.BatchCreateOrUpdateRiskCategoryDTO;
import org.dows.hep.api.base.risk.request.BatchCreateOrUpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.CreateRiskCategoryRequest;
import org.dows.hep.api.base.risk.request.UpdateRiskCategoryRequest;
import org.dows.hep.api.base.risk.response.RiskCategoryResponse;
import org.dows.hep.api.enums.EnumESC;
import org.dows.hep.api.enums.EnumRedissonLock;
import org.dows.hep.api.exception.RiskCategoryException;
import org.dows.hep.biz.util.RedissonUtil;
import org.dows.hep.entity.RiskCategoryEntity;
import org.dows.hep.entity.RiskModelEntity;
import org.dows.hep.service.RiskCategoryService;
import org.dows.hep.service.RiskModelService;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
* @description project descr:风险:风险类别
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class RiskCategoryBiz{

    @Value("${redisson.lock.lease-time.teacher.risk-category-create-delete-update:5000}")
    private Integer leaseTimeRiskCategoryCreateDeleteUpdate;
    private final String riskCategoryFieldPid = "pid";
    private final RiskCategoryService riskCategoryService;
    private final RedissonClient redissonClient;
    private final IdGenerator idGenerator;

    private final RiskModelService riskModelService;

    @Transactional(rollbackFor = Exception.class)
    public void batchCreateOrUpdateRiskCategory(BatchCreateOrUpdateRiskCategoryRequest batchCreateOrUpdateRiskCategoryRequest) throws InterruptedException {
        String appId = batchCreateOrUpdateRiskCategoryRequest.getAppId();
        String pid = batchCreateOrUpdateRiskCategoryRequest.getPid();
        RLock lock = redissonClient.getLock(RedissonUtil.getLockName(appId, EnumRedissonLock.INDICATOR_CATEGORY_CREATE_DELETE_UPDATE, riskCategoryFieldPid, pid));
        boolean isLocked = lock.tryLock(leaseTimeRiskCategoryCreateDeleteUpdate, TimeUnit.MILLISECONDS);
        if (!isLocked) {
            throw new RiskCategoryException(EnumESC.SYSTEM_BUSY_PLEASE_OPERATOR_INDICATOR_CATEGORY_LATER);
        }
        try {
            if (Objects.nonNull(pid)) {
                riskCategoryService.lambdaQuery()
                    .eq(RiskCategoryEntity::getRiskCategoryId, pid)
                    .oneOpt()
                    .orElseThrow(() -> {
                        log.warn("方法batchCreateOrUpdateRiskCategory的参数batchCreateOrUpdateRiskCategory的pid：{}在数据库不存在", pid);
                        return new RiskCategoryException(EnumESC.VALIDATE_EXCEPTION);
                    });
            }
            List<RiskCategoryEntity> createRiskCategoryEntityList = new ArrayList<>();
            List<RiskCategoryEntity> updateRiskCategoryEntityList = new ArrayList<>();
            List<BatchCreateOrUpdateRiskCategoryDTO> batchCreateOrUpdateRiskCategoryDTOList = batchCreateOrUpdateRiskCategoryRequest.getBatchCreateOrUpdateRiskCategoryDTOList();
            List<String> paramRiskCategoryIdList = batchCreateOrUpdateRiskCategoryDTOList
                .stream()
                .map(BatchCreateOrUpdateRiskCategoryDTO::getRiskCategoryId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
            List<String> dbRiskCategoryIdList = new ArrayList<>();
            Map<String, RiskCategoryEntity> kRiskCategoryIdVRiskCategoryEntityMap = riskCategoryService.lambdaQuery()
                .eq(RiskCategoryEntity::getAppId, appId)
                .eq(RiskCategoryEntity::getPid, pid)
                .list()
                .stream()
                .peek(riskCategoryEntity -> dbRiskCategoryIdList.add(riskCategoryEntity.getRiskCategoryId()))
                .collect(Collectors.toMap(RiskCategoryEntity::getRiskCategoryId, a -> a));
            if (paramRiskCategoryIdList.size() != dbRiskCategoryIdList.size()) {
                log.warn("方法batchCreateOrUpdateRiskCategory的参数batchCreateOrUpdateRiskCategory传过来的修改类别数量与数据库不一致");
                throw new RiskCategoryException(EnumESC.VALIDATE_EXCEPTION);
            }
            if (paramRiskCategoryIdList.parallelStream()
                .anyMatch(riskCategoryId -> !dbRiskCategoryIdList.contains(riskCategoryId))
            ) {
                log.warn("方法batchCreateOrUpdateRiskCategory的参数batchCreateOrUpdateRiskCategory传过来的RiskCategoryIdList存在数据库不存在的。" +
                    "paramRiskCategoryIdList：{}, dbRiskCategoryIdList:{}", paramRiskCategoryIdList, dbRiskCategoryIdList);
                throw new RiskCategoryException(EnumESC.VALIDATE_EXCEPTION);
            }
            AtomicInteger atomicInteger = new AtomicInteger(1);
            batchCreateOrUpdateRiskCategoryDTOList
                .forEach(batchCreateOrUpdateRiskCategoryDTO -> {
                    String riskCategoryId = batchCreateOrUpdateRiskCategoryDTO.getRiskCategoryId();
                    if (Objects.isNull(riskCategoryId)) {
                        createRiskCategoryEntityList.add(
                            RiskCategoryEntity
                                .builder()
                                .riskCategoryId(idGenerator.nextIdStr())
                                .appId(appId)
                                .pid(pid)
                                .riskCategoryName(batchCreateOrUpdateRiskCategoryDTO.getRiskCategoryName())
                                .seq(atomicInteger.getAndIncrement())
                                .build());
                    } else {
                        RiskCategoryEntity riskCategoryEntity = kRiskCategoryIdVRiskCategoryEntityMap.get(batchCreateOrUpdateRiskCategoryDTO.getRiskCategoryId());
                        riskCategoryEntity.setRiskCategoryName(batchCreateOrUpdateRiskCategoryDTO.getRiskCategoryName());
                        riskCategoryEntity.setSeq(atomicInteger.getAndIncrement());
                        updateRiskCategoryEntityList.add(riskCategoryEntity);
                    }
                });
            if (!createRiskCategoryEntityList.isEmpty()) {
                riskCategoryService.saveBatch(createRiskCategoryEntityList);
            }
            if (!updateRiskCategoryEntityList.isEmpty()) {
                riskCategoryService.updateBatchById(updateRiskCategoryEntityList);
            }
        } finally {
            lock.unlock();
        }
    }
    
    /**
    * @param
    * @return
    * @说明: 创建风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void createRiskCategory(CreateRiskCategoryRequest createRiskCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 删除风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void deleteRiskCategory(String riskCategoryId) {
        boolean isRefByRiskCategory = riskCategoryService.lambdaQuery()
            .eq(RiskCategoryEntity::getPid, riskCategoryId)
            .exists();
        if (isRefByRiskCategory) {
            throw new RiskCategoryException(EnumESC.INDICATOR_CATEGORY_HAS_DATA_CANNOT_DELETE);
        }
        boolean isRefByRiskModel = riskModelService.lambdaQuery()
            .eq(RiskModelEntity::getRiskCategoryId, riskCategoryId)
            .exists();
        if (isRefByRiskModel) {
            throw new RiskCategoryException(EnumESC.INDICATOR_CATEGORY_HAS_DATA_CANNOT_DELETE);
        }
        boolean isSuccess = riskCategoryService.remove(
            new LambdaQueryWrapper<RiskCategoryEntity>()
                .eq(RiskCategoryEntity::getRiskCategoryId, riskCategoryId)
        );
        if (!isSuccess) {
            throw new RiskCategoryException(EnumESC.INDICATOR_CATEGORY_DELETE_FAILED);
        }
    }
    /**
    * @param
    * @return
    * @说明: 更改风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateRiskCategory(UpdateRiskCategoryRequest updateRiskCategory ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public RiskCategoryResponse getRiskCategory(String riskCategoryId ) {
        return new RiskCategoryResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<RiskCategoryResponse> listRiskCategory(String appId, String riskRiskCategoryName ) {
        return new ArrayList<RiskCategoryResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选风险类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageRiskCategory(Integer pageNo, Integer pageSize, String appId, String riskRiskCategoryName ) {
        return new String();
    }
}