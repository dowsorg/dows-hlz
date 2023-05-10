package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
* @description project descr:实验:机构操作
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentOrgBiz{
    private final ExperimentPersonService experimentPersonService;
    /**
    * @param
    * @return
    * @说明: 获取机构人物列表，挂号费用，挂号状态
    * @关联表: ExperimentPerson
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgPersonResponse pageOrgPersons(FindOrgPersonsRequest findOrgPersons ) {
        return new OrgPersonResponse();
    }
    /**
    * @param
    * @return
    * @说明: 挂号：医院，体检中心
    * @关联表: ExperimentPerson,OperateFlow
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean startOrgFlow(StartOrgFlowRequest startOrgFlow ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 获取机构通知列表
    * @关联表: ExperimentOrgNotice
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgNoticeResponse pageOrgNotice(FindOrgNoticeRequest findOrgNotice ) {
        return new OrgNoticeResponse();
    }
    /**
    * @param
    * @return
    * @说明: 获取机构报告列表
    * @关联表: OperateFlow
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public OrgReportResponse pageOrgReport(FindOrgReportRequest findOrgReport ) {
        return new OrgReportResponse();
    }
    /**
    * @param
    * @return
    * @说明: 查看体检报告详情
    * @关联表: OperateFlow,OperateFlowSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public PhysicalExamReportInfoResponse getPhysicalExamReport(String operateFlowId ) {
        return new PhysicalExamReportInfoResponse();
    }
    /**
    * @param
    * @return
    * @说明: 查看诊疗报告详情
    * @关联表: OperateFlow,OperateFlowSnap
    * @工时: 6H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public TreatReportInfoResponse getTreatReport(String operateFlowId ) {
        return new TreatReportInfoResponse();
    }

    /**
     * @param
     * @return
     * @说明: 获取实验人物列表
     * @关联表: experiment_person
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年5月10日 上午10:11:34
     */
    public IPage<ExperimentPersonResponse> pageExperimentPersons(ExperimentPersonRequest personRequest) {
        List<ExperimentPersonResponse> responseList = new ArrayList<>();
        LambdaQueryWrapper<ExperimentPersonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExperimentPersonEntity::getExperimentOrgId, personRequest.getExperimentOrgId())
                .orderByDesc(ExperimentPersonEntity::getDt);
        Page<ExperimentPersonEntity> page = new Page<>(personRequest.getPageNo(), personRequest.getPageSize());
        IPage<ExperimentPersonEntity> entityIPage = experimentPersonService.page(page, queryWrapper);
        //复制
        IPage<ExperimentPersonResponse> voPage = new Page<>();
        BeanUtils.copyProperties(entityIPage, voPage, new String[]{"records"});
        for(ExperimentPersonEntity entity : entityIPage.getRecords()){
            ExperimentPersonResponse person = new ExperimentPersonResponse();
            BeanUtil.copyProperties(entity,person);
            person.setId(entity.getId().toString());
            responseList.add(person);
        }
        voPage.setRecords(responseList);
        return voPage;
    }
}