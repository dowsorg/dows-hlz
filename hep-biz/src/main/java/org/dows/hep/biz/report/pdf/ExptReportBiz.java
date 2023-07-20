package org.dows.hep.biz.report.pdf;

import cn.hutool.core.util.StrUtil;
import com.itextpdf.commons.utils.Base64;
import org.apache.commons.io.IOUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.properties.FindSoftProperties;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.vo.report.ExptBaseInfoModel;
import org.dows.hep.vo.report.ExptReportModel;
import org.dows.hep.vo.report.ExptReportVO;
import org.slf4j.Logger;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告
 * @date 2023/7/7 13:49
 **/
public interface ExptReportBiz<P extends ExptReportBiz.ExptReportData, R extends ExptReportModel> {
    ExptReportVO generatePdfReport(String exptInstanceId, String exptGroupId);

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
     * @param experimentGroupService - 实验小组 service
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId - 实验小组ID
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
     * @param exptInstanceId - 实验实例ID
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
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId - 实验小组ID
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

    /**
     * @param findSoftProperties - findSoft 公司基本信息
     * @param log - log
     * @return org.dows.hep.vo.report.ExptBaseInfoModel
     * @author fhb
     * @description 生成一些基本信息
     * @date 2023/7/20 11:12
     */
    default ExptBaseInfoModel generateBaseInfoVO(FindSoftProperties findSoftProperties, Logger log) {
        String logoStr = null;
        String coverStr = null;
        try {
            logoStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getLogo()).getInputStream()));
            coverStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getCover()).getInputStream()));
        } catch (IOException e) {
            log.error("导出实验报告时，获取logo和cover图片资源异常");
            throw new BizException("导出实验报告时，获取logo和cover图片资源异常");
        }

        return ExptBaseInfoModel.builder()
                .title(findSoftProperties.getExptSchemeReportTitle())
                .logoImg(logoStr)
                .coverImg(coverStr)
                .copyRight(findSoftProperties.getCopyRight())
                .build();
    }

    public interface ExptReportData {

    }
}
