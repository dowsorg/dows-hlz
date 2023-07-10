package org.dows.hep.rest.report.pdf;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.constant.RedisKeyConst;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.report.pdf.ExptGroupReportVO;
import org.dows.hep.api.report.pdf.ExptReportVO;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.biz.report.pdf.ExptOverviewReportBiz;
import org.dows.hep.biz.report.pdf.ExptSandReportBiz;
import org.dows.hep.biz.report.pdf.ExptSchemeReportBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author fhb
 * @version 1.0
 * @description 报告管理-pdf形式
 * @date 2023/7/6 16:38
 **/
@RequiredArgsConstructor
@RestController
@Tag(name = "pdf报告管理", description = "pdf报告管理")
public class ExptReportPdfRest {
    private final RedissonClient redissonClient;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExptSchemeReportBiz exptSchemeReportBiz;
    private final ExptSandReportBiz exptSandReportBiz;
    private final ExptOverviewReportBiz exptOverviewReportBiz;


    @Operation(summary = "导出小组实验pdf报告")
    @GetMapping(value = "/v1/report/pdf/exportGroupReport")
    public ExptReportVO exportGroupReport(@RequestParam String experimentInstanceId, @RequestParam String experimentGroupId) {
        // 获取实验信息
        ExperimentInstanceEntity exptEntity = checkExpt(experimentInstanceId);
        String zipPath = SystemConstant.PDF_REPORT_ZIP_PATH + exptEntity.getId() + SystemConstant.SPLIT_UNDER_LINE + exptEntity.getExperimentName() + SystemConstant.SPLIT_UNDER_LINE + experimentGroupId + SystemConstant.SUFFIX_ZIP;

        /*todo 分布式锁*/
        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + experimentGroupId);
        try {
            if (lock.tryLock(-1, 10, TimeUnit.SECONDS)) {
                ExptReportVO exptReportVO = generatePdf(experimentInstanceId, experimentGroupId);
                toZip(exptReportVO, zipPath);
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    @Operation(summary = "导出实验pdf报告")
    @GetMapping(value = "/v1/report/pdf/exportExptReport")
    public ExptReportVO exportExptReport(@RequestParam String experimentInstanceId) {
        // 获取实验信息
        ExperimentInstanceEntity exptEntity = checkExpt(experimentInstanceId);
        String zipPath = SystemConstant.PDF_REPORT_ZIP_PATH + exptEntity.getId() + SystemConstant.SPLIT_UNDER_LINE + exptEntity.getExperimentName() + SystemConstant.SUFFIX_ZIP;
        // 如果已经存在直接返回
        File zipFile = new File(zipPath);
        if (zipFile.exists()) {
            return ExptReportVO.builder()
                    .zipPath(zipPath)
                    .groupReportList(new ArrayList<>())
                    .build();
        }

        /*todo 分布式锁*/
        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + experimentInstanceId);
        try {
            if (lock.tryLock(-1, 30, TimeUnit.SECONDS)) {
                ExptReportVO exptReportVO = generatePdf(experimentInstanceId, null);
                toZip(exptReportVO, zipPath);
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    private ExperimentInstanceEntity checkExpt(String experimentInstanceId) {
        ExperimentInstanceEntity exptInstance = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElseThrow(() -> new BizException("实验不存在"));
        Integer state = exptInstance.getState();
        if (state < EnumExperimentState.FINISH.getState()) {
            throw new BizException("实验还未结束，请等待");
        }
        return exptInstance;
    }

    // 以小组为单位导出PDF
    private ExptReportVO generatePdf(String experimentInstanceId, String experimentGroupId) {
        List<ExptGroupReportVO> exptGroupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(exptGroupReportVOS)
                .build();

        // 小组的实验总报告
        ExptReportVO overviewReportVO = exptOverviewReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
        exptGroupReportVOS.addAll(overviewReportVO.getGroupReportList());

        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(experimentInstanceId);
        switch (exptSettingMode) {
            case SCHEME -> {
                ExptReportVO schemeReportVO = exptSchemeReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
            }
            case SAND -> {
                ExptReportVO sandReportVO = exptSandReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
            case SAND_SCHEME -> {
                ExptReportVO schemeReportVO = exptSchemeReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                ExptReportVO sandReportVO = exptSandReportBiz.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
        }

        return result;
    }

    private void toZip(ExptReportVO reportVO, String zipPath) {
        byte[] buf = new byte[1024];
        ZipOutputStream zos = null;
        try {
            FileOutputStream out = new FileOutputStream(zipPath);
            zos = new ZipOutputStream(out);

            List<ExptGroupReportVO> groupReportList = reportVO.getGroupReportList();
            for (ExptGroupReportVO groupReportVO : groupReportList) {
                Integer exptGroupNo = groupReportVO.getExptGroupNo();
                List<String> paths = groupReportVO.getPaths();

                String groupDirName = "第" + exptGroupNo + "组";
                for (String path : paths) {
                    File file = new File(path);
                    zos.putNextEntry(new ZipEntry(groupDirName + "/" + file.getName()));
                    int len;
                    FileInputStream in = new FileInputStream(file);
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }
                    zos.closeEntry();
                    in.close();
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException("zip error: ", e1);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e2) {
                    throw new RuntimeException("zip close error: ", e2);
                }
            }
        }
        reportVO.setZipPath(zipPath);
    }
}
