package org.dows.hep.biz.tenant.experiment;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.uim.AccountInfo;
import org.dows.hep.api.tenant.experiment.request.CreateExperimentRequest;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.tenant.experiment.request.GroupSettingRequest;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentSettingService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:实验:实验管理
 * @date 2023年4月18日 上午10:45:07
 */
@RequiredArgsConstructor
@Service
public class ExperimentManageBiz {
    // 实验实例
    private final ExperimentInstanceService experimentInstanceService;
    // 实验设置
    private final ExperimentSettingService experimentSettingService;
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;
    private final IdGenerator idGenerator;

//    private final

    /**
     * @param
     * @return
     * @说明: 分配实验
     * @关联表: ExperimentInstance, experimentSetting
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    @Transactional
    public String experimentAllot(CreateExperimentRequest createExperiment) {
        ExperimentInstanceEntity experimentInstance = ExperimentInstanceEntity.builder()
                .experimentInstanceId(idGenerator.nextIdStr())
                .startTime(createExperiment.getStartTime())
                .experimentName(createExperiment.getExperimentName())
                .experimentDescr(createExperiment.getExperimentDescr())
                .state(0)
                .caseInstanceId(createExperiment.getCaseInstanceId())
                .caseName(createExperiment.getCaseName())
                .build();
        // 保存实验实例
        experimentInstanceService.saveOrUpdate(experimentInstance);

        ExperimentSetting experimentSetting = createExperiment.getExperimentSetting();
        List<AccountInfo> teachers = createExperiment.getTeachers();
        List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
        for (AccountInfo teacher : teachers) {
            ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                    .experimentParticipatorId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    // todo UIM
                    .accountId(teacher.getId() + "")
                    .accountName(teacher.getAccountName())
                    .participatorType(0)
                    .build();
            experimentParticipatorEntityList.add(experimentParticipatorEntity);
        }
        // 保存实验参与人
        experimentParticipatorService.saveOrUpdateBatch(experimentParticipatorEntityList);

        // 标准模式
        if (null != experimentSetting.getSchemeSetting() && null != experimentSetting.getSandSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();
            //保存方案设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);

            experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();
            //保存沙盘设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
            // 沙盘模式
        } else if (null != experimentSetting.getSandSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSandSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSandSetting()))
                    .build();
            //保存沙盘设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
            // 方案设计模式i
        } else if (null != experimentSetting.getSchemeSetting()) {
            ExperimentSettingEntity experimentSettingEntity = ExperimentSettingEntity.builder()
                    .experimentSettingId(idGenerator.nextIdStr())
                    .experimentInstanceId(experimentInstance.getExperimentInstanceId())
                    .configKey(experimentSetting.getSchemeSetting().getClass().getName())
                    .configJsonVals(JSONUtil.toJsonStr(experimentSetting.getSchemeSetting()))
                    .build();
            //保存方案设计
            experimentSettingService.saveOrUpdate(experimentSettingEntity);
        }
        return experimentInstance.getExperimentInstanceId();
    }

    /**
     * @param
     * @return
     * @说明: 实验分组ss
     * @关联表: experimentGroup, experimentParticipator
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public Boolean experimentGrouping(GroupSettingRequest groupSetting) {

        ExperimentGroupEntity experimentGroupEntity = ExperimentGroupEntity.builder()
                .experimentGroupId(idGenerator.nextIdStr())
                .experimentInstanceId(groupSetting.getExperimentInstanceId())
                .groupAlias(groupSetting.getGroupAlias())
                .memberCount(groupSetting.getMemberCount())
                .build();

        // 保存实验小组
        experimentGroupService.saveOrUpdate(experimentGroupEntity);
        //todo
        List<ExperimentParticipatorEntity> experimentParticipatorEntityList = new ArrayList<>();
        List<GroupSettingRequest.ExperimentParticipator> experimentParticipators = groupSetting.getExperimentParticipators();
        for (GroupSettingRequest.ExperimentParticipator experimentParticipator : experimentParticipators) {
            ExperimentParticipatorEntity experimentParticipatorEntity = ExperimentParticipatorEntity.builder()
                    .experimentParticipatorId(idGenerator.nextIdStr())
                    .experimentInstanceId(groupSetting.getExperimentInstanceId())
                    .accountId(experimentParticipator.getParticipatorId())
                    .accountName(experimentParticipator.getParticipatorName())
                    .experimentGroupId(experimentGroupEntity.getExperimentGroupId())
                    .participatorType(1)
                    .build();
            // 如果是0【第一个人】设置为组长
            if (experimentParticipator.getSeq() == 0) {
                experimentParticipatorEntity.setParticipatorType(2);
            }
            experimentParticipatorEntityList.add(experimentParticipatorEntity);
        }
        // 保存实验参与人[学生]
        return experimentParticipatorService.saveOrUpdateBatch(experimentParticipatorEntityList);
    }

    /**
     * @param
     * @return
     * @说明: 获取实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public List<ExperimentListResponse> listExperiment() {


        return new ArrayList<ExperimentListResponse>();
    }


    /**
     * @param
     * @return
     * @说明: 分页实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public IPage<ExperimentListResponse> pageExperiment(PageExperimentRequest pageExperimentRequest) {

        Page page = new Page<ExperimentInstanceEntity>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.addOrder(pageExperimentRequest.getDesc() ?
                OrderItem.desc(pageExperimentRequest.getOrderBy()) : OrderItem.asc(pageExperimentRequest.getOrderBy()));
        Page page1 = experimentInstanceService.page(page, experimentInstanceService.lambdaQuery()
                .likeLeft(ExperimentInstanceEntity::getExperimentName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getCaseName, pageExperimentRequest.getKeyword())
                .likeLeft(ExperimentInstanceEntity::getExperimentDescr, pageExperimentRequest.getKeyword()));
        return page1;
    }
}