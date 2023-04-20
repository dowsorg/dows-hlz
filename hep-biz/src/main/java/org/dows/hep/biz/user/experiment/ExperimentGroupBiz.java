package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.CreateGroupRequest;
import org.dows.hep.api.user.experiment.response.ExperimentGroupResponse;
import org.dows.hep.api.user.experiment.response.ExperimentParticipatorResponse;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;

/**
* @description project descr:实验:实验小组
*
* @author lait.zhang
* @date 2023年4月18日 上午10:45:07
*/
@RequiredArgsConstructor
@Service
public class ExperimentGroupBiz{

    private final ExperimentGroupService experimentGroupService;

    private final ExperimentParticipatorService experimentParticipatorService;

    /**
    * @param
    * @return
    * @说明: 创建团队
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public Boolean createGroup(CreateGroupRequest createGroup ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取实验小组列表
    * @关联表: 
    * @工时: 0H
    * @开发者: 
    * @开始时间: 
    * @创建时间: 2023年4月18日 上午10:45:07
    */
    public ExperimentGroupResponse listGroup(String experimentInstanceId ) {

        //experimentParticipatorService.page(experimentParticipatorService.lambdaQuery().eq(ExperimentParticipatorEntity::getExperimentInstanceId,experimentInstanceId));

        // todo

        return new ExperimentGroupResponse();
    }

    /**
     * @param
     * @return
     * @说明: 获取实验小组列表
     * @关联表:
     * @工时: 0H
     * @开发者:
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<ExperimentParticipatorResponse> pageParticipators(Page<ExperimentParticipatorEntity> page,String experimentInstanceId ) {
        LambdaQueryChainWrapper<ExperimentParticipatorEntity> eq = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .groupBy(ExperimentParticipatorEntity::getExperimentGroupId)
                .orderByAsc(ExperimentParticipatorEntity::getGroupNo);
        Page page1 = experimentParticipatorService.page(page, eq);
        return page1;
    }
}