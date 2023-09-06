package org.dows.hep.biz.report;

import cn.hutool.core.util.StrUtil;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.vo.report.ExptReportModel;
import org.dows.hep.vo.report.ExptReportVO;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告
 * @date 2023/7/7 13:49
 **/
public interface ExptReportHandler<P extends ExptReportHandler.ExptReportData, R extends ExptReportModel> {
    ExptReportVO generatePdfReport(String exptInstanceId, String exptGroupId, boolean regenerate);

    /**
     * 生成 pdf 文件
     * - prepareData: 预先准备好需要的业务数据
     * - getExptReportModel: 将业务数据组装为 modelAndView 中的 model
     * - getSchemeFlt: 获取 modelAndView 中的 view
     * - getOutputPosition: 获取 pdf 输出位置
     */
    P prepareData(String exptInstanceId, String exptGroupId);

    R convertData2Model(String exptGroupId, P exptReportData);

    String getSchemeFlt();

    String getOutputPosition(String exptGroupId, P exptReportData);

    /**
     * @param exptInstanceId - 实验示例ID
     * @param exptGroupId    - 实验小组ID
     * @return R - 返回报告需要的数据
     * @author fhb
     * @description 返回小组报告需要的数据
     * @date 2023/9/6 16:20
     */
    default R getPdfData(String exptInstanceId, String exptGroupId) {
        P p = prepareData(exptInstanceId, exptGroupId);
        return convertData2Model(exptGroupId, p);
    }

    /**
     * @param experimentGroupService - 实验小组 service
     * @param exptInstanceId         - 实验实例ID
     * @param exptGroupId            - 实验小组ID
     * @return java.util.List<org.dows.hep.entity.ExperimentGroupEntity>
     * @author fhb
     * @description 列出`exptInstanceId` `exptGroupId` 下小组信息
     * @date 2023/7/20 11:08
     */
    default List<ExperimentGroupEntity> listExptGroupInfo(ExperimentGroupService experimentGroupService, String exptInstanceId, String exptGroupId) {
        return experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentGroupEntity::getExperimentGroupId, exptGroupId)
                .list();
    }

    /**
     * @param experimentInstanceService - 实验实例 service
     * @param exptInstanceId            - 实验实例ID
     * @return org.dows.hep.entity.ExperimentInstanceEntity
     * @author fhb
     * @description 获取实验信息
     * @date 2023/7/20 11:10
     */
    default ExperimentInstanceEntity getExptInfo(ExperimentInstanceService experimentInstanceService, String exptInstanceId) {
        return experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .oneOpt()
                .orElse(null);
    }

    /**
     * @param experimentParticipatorService - 实验参与者 service
     * @param exptInstanceId                - 实验实例ID
     * @param exptGroupId                   - 实验小组ID
     * @return java.util.List<org.dows.hep.entity.ExperimentParticipatorEntity>
     * @author fhb
     * @description 获取  `exptInstanceId`  `exptGroupId` 下的参与者信息
     * @date 2023/7/20 11:10
     */
    default List<ExperimentParticipatorEntity> listExptMembers(ExperimentParticipatorService experimentParticipatorService, String exptInstanceId, String exptGroupId) {
        return experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentParticipatorEntity::getExperimentGroupId, exptGroupId)
                .list();
    }

    public interface ExptReportData {

    }
}
