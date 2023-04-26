package org.dows.hep.biz.base.indicator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.CreateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.request.UpdateIndicatorFuncRequest;
import org.dows.hep.api.base.indicator.response.IndicatorFuncResponse;
import org.dows.hep.service.IndicatorFuncService;
import org.dows.sequence.api.IdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:指标:指标功能
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class IndicatorFuncBiz{
    @Value("${redisson.lock.lease-time.teacher.indicator-func-create-delete-update:5000}")
    private Integer leaseTimeIndicatorFuncCreateDeleteUpdate;

    private final String indicatorFuncFieldIndicatorCategoryId = "indicator_category_id";
    private final IdGenerator idGenerator;
    private final RedissonClient redissonClient;
    private final IndicatorFuncService indicatorFuncService;
    /**
    * @param
    * @return
    * @说明: 创建指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    @Transactional(rollbackFor = Exception.class)
    public void createIndicatorFunc(CreateIndicatorFuncRequest createIndicatorFuncRequest) {
        String indicatorCategoryId = createIndicatorFuncRequest.getIndicatorCategoryId();
    }
    /**
    * @param
    * @return
    * @说明: 删除指标功能
    * @关联表: 
    * @工时: 2H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void deleteIndicatorFunc(String indicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 更新指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public void updateIndicatorFunc(UpdateIndicatorFuncRequest updateIndicatorFunc ) {
        
    }
    /**
    * @param
    * @return
    * @说明: 获取指标功能
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public IndicatorFuncResponse getIndicatorFunc(String indicatorFunc ) {
        return new IndicatorFuncResponse();
    }
    /**
    * @param
    * @return
    * @说明: 筛选指标类别
    * @关联表: 
    * @工时: 4H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<IndicatorFuncResponse> listIndicatorFunc(String appId, String indicatorCategoryId, String name ) {
        return new ArrayList<IndicatorFuncResponse>();
    }
    /**
    * @param
    * @return
    * @说明: 分页筛选指标类别
    * @关联表: 
    * @工时: 5H
    * @开发者: runsix
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public String pageIndicatorFunc(Integer pageNo, Integer pageSize, String appId, String indicatorCategoryId, String name ) {
        return new String();
    }
}