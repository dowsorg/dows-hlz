package org.dows.hep.biz.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.materials.response.MaterialsAttachmentResponse;
import org.dows.hep.api.base.materials.response.MaterialsResponse;
import org.dows.hep.api.user.experiment.ExptReportTypeEnum;
import org.dows.hep.biz.base.materials.MaterialsManageBiz;
import org.dows.hep.biz.user.experiment.ExperimentReportBiz;
import org.dows.hep.entity.ExperimentReportInstanceEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 报告记录
 * @date 2023/7/21 11:29
 **/

@Slf4j
@AllArgsConstructor
@Component
public class ReportRecordHelper {
    private final ExperimentReportBiz experimentReportBiz;
    private final MaterialsManageBiz materialsManageBiz;

    /**
     * materials && materialsItem && exptReportInstance
     *
     * @param exptInstanceId   - 实验实例ID
     * @param exptGroupId      - 实验小组ID
     * @param reportTypeEnum   - 报告类型枚举
     * @param materialsRequest - 素材
     * @return boolean
     * @author fhb
     * @description 将报告的下载地址进行记录 - 后续有必要可以加缓存
     * @date 2023/8/1 9:18
     */
    @DSTransactional
    public boolean record(String exptInstanceId, String exptGroupId, ExptReportTypeEnum reportTypeEnum, MaterialsRequest materialsRequest) {
        String materialsId = materialsManageBiz.saveOrUpdMaterials(materialsRequest);

        ExperimentReportInstanceEntity exptReportInstanceEntity = ExperimentReportInstanceEntity.builder()
                .experimentInstanceId(exptInstanceId)
                .experimentGroupId(exptGroupId)
                .materialsId(materialsId)
                .reportType(reportTypeEnum.name())
                .build();
        boolean saveRes = experimentReportBiz.saveReport(exptReportInstanceEntity);

        if (StrUtil.isNotBlank(materialsId) && saveRes) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * exptInstanceId && typeEnum 最多匹配一条数据
     *
     * @param exptInstanceId - 实验实例ID
     * @param typeEnum       - 报告类型
     * @return java.lang.String
     * @author fhb
     * @description 获取 `该实验` 指定类型的报告 - exptInstanceId && typeEnum 最多匹配一条数据
     * @date 2023/8/1 9:21
     */
    public String getReportOfExpt(String exptInstanceId, ExptReportTypeEnum typeEnum) {
        ExperimentReportInstanceEntity reportOfExpt = experimentReportBiz.getReportOfExpt(exptInstanceId, typeEnum);
        return getFileUri(reportOfExpt);
    }


    /**
     * exptInstanceId && exptGroupId && typeEnum 最多匹配一条数据
     *
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @param typeEnum       - 实验报告类型
     * @return java.lang.String
     * @author fhb
     * @description 获取 `该实验` `该小组` 指定类型的报告 - exptInstanceId && exptGroupId && typeEnum 最多匹配一条数据
     * @date 2023/8/1 9:22
     */
    public String getReportOfGroup(String exptInstanceId, String exptGroupId, ExptReportTypeEnum typeEnum) {
        ExperimentReportInstanceEntity reportOfGroup = experimentReportBiz.getReportOfGroup(exptInstanceId, exptGroupId, typeEnum);
        return getFileUri(reportOfGroup);
    }

    /**
     * exptInstanceId && accountId && typeEnum 最多匹配一条数据
     *
     * @param exptInstanceId - 实验实例ID
     * @param accountId      - 学生账号ID
     * @param typeEnum       - 实验报告类型
     * @return java.lang.String
     * @author fhb
     * @description 获取 `该实验` `该学生` 指定类型的报告 - exptInstanceId && accountId && typeEnum 最多匹配一条数据
     * @date 2023/8/1 9:24
     */
    public String getReportOfAccount(String exptInstanceId, String accountId, ExptReportTypeEnum typeEnum) {
        ExperimentReportInstanceEntity reportOfAccount = experimentReportBiz.getReportOfAccount(exptInstanceId, accountId, typeEnum);
        return getFileUri(reportOfAccount);
    }

    private String getFileUri(ExperimentReportInstanceEntity reportOfExpt) {
        if (BeanUtil.isEmpty(reportOfExpt)) {
            return "";
        }

        String materialsId = reportOfExpt.getMaterialsId();
        MaterialsResponse materials = materialsManageBiz.getMaterials(materialsId);
        if (BeanUtil.isEmpty(materials)) {
            return "";
        }

        List<MaterialsAttachmentResponse> materialsAttachments = materials.getMaterialsAttachments();
        if (CollUtil.isEmpty(materialsAttachments)) {
            return "";
        }

        MaterialsAttachmentResponse materialsAttachmentResponse = materialsAttachments.get(0);
        return materialsAttachmentResponse.getFileUri();
    }
}
