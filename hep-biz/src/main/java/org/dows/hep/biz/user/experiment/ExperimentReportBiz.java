package org.dows.hep.biz.user.experiment;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExptReportTypeEnum;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentReportInstanceEntity;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentReportInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

/**
 * 实验报告BIZ
 * todo
 * get/getByGroupId/获取实验小组报告
 * get/page/分页获取实验报告列表
 * get/getByAccountId/获取用户实验报告
 *
 */
@AllArgsConstructor
@Service
@Slf4j
public class ExperimentReportBiz {

    private final IdGenerator idGenerator;
    private final ExperimentReportInstanceService experimentReportInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;

    /**
     * @param entity - 实体
     * @return java.lang.String
     * @author fhb
     * @description 保存报告记录
     * @date 2023/7/21 13:31
     */
    public boolean saveReport(ExperimentReportInstanceEntity entity) {
        String experimentReportInstanceId = entity.getExperimentReportInstanceId();
        if (StrUtil.isBlank(experimentReportInstanceId)) {
            entity.setExperimentReportInstanceId(idGenerator.nextIdStr());
        }
        return experimentReportInstanceService.save(entity);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param typeEnum - 类型
     * @return org.dows.hep.entity.ExperimentReportInstanceEntity
     * @author fhb
     * @description 获取实验报告
     * @date 2023/7/21 11:50
     */
    public ExperimentReportInstanceEntity getReportOfExpt(String exptInstanceId, ExptReportTypeEnum typeEnum) {
        return experimentReportInstanceService.lambdaQuery()
                .eq(ExperimentReportInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .eq( ExperimentReportInstanceEntity::getReportType, typeEnum.name())
                .oneOpt()
                .orElse(null);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @param typeEnum       - 类型
     * @return org.dows.hep.entity.ExperimentReportInstanceEntity
     * @author fhb
     * @description
     * @date 2023/7/21 11:51
     */
    public ExperimentReportInstanceEntity getReportOfGroup(String exptInstanceId, String exptGroupId, ExptReportTypeEnum typeEnum) {
        return experimentReportInstanceService.lambdaQuery()
                .eq(ExperimentReportInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentReportInstanceEntity::getExperimentGroupId, exptGroupId)
                .eq(ExperimentReportInstanceEntity::getReportType, typeEnum.name())
                .oneOpt()
                .orElse(null);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param accountId - 账号ID
     * @param typeEnum - 类型
     * @return org.dows.hep.entity.ExperimentReportInstanceEntity
     * @author fhb
     * @description
     * @date 2023/7/21 13:30
     */
    public ExperimentReportInstanceEntity getReportOfAccount(String exptInstanceId, String accountId, ExptReportTypeEnum typeEnum) {
        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .oneOpt()
                .orElseThrow(() -> new BizException("获取用户实验报告时, 获取实验参与者信息异常"));
        String experimentGroupId = experimentParticipatorEntity.getExperimentGroupId();
        return getReportOfGroup(exptInstanceId, experimentGroupId, typeEnum);
    }

    public boolean delReportOfExpt(String exptInstanceId, ExptReportTypeEnum typeEnum) {
        return experimentReportInstanceService.lambdaUpdate()
                .eq(ExperimentReportInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentReportInstanceEntity::getReportType, typeEnum.name())
                .set(ExperimentReportInstanceEntity::getDeleted, true)
                .update();
    }

    public boolean delReportOfGroup(String exptInstanceId, String exptGroupId, ExptReportTypeEnum typeEnum) {
        return experimentReportInstanceService.lambdaUpdate()
                .eq(ExperimentReportInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentReportInstanceEntity::getExperimentGroupId, exptGroupId)
                .eq(ExperimentReportInstanceEntity::getReportType, typeEnum.name())
                .set(ExperimentReportInstanceEntity::getDeleted, true)
                .update();
    }

    public boolean delReportOfAccount(String exptInstanceId, String accountId, ExptReportTypeEnum typeEnum) {
        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .oneOpt()
                .orElseThrow(() -> new BizException("获取用户实验报告时, 获取实验参与者信息异常"));
        String experimentGroupId = experimentParticipatorEntity.getExperimentGroupId();
        return delReportOfAccount(exptInstanceId, experimentGroupId, typeEnum);
    }
}
