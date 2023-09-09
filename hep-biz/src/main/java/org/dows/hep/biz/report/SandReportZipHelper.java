package org.dows.hep.biz.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/9 20:49
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class SandReportZipHelper {
    private final ReportOSSHelper ossHelper;
    private static final String ZIP_DIR = "zip";
    private static final String LOCAL_ZIP_DIR = SystemConstant.PDF_REPORT_TMP_PATH + ZIP_DIR + File.separator;

    public boolean zipAndUploadV2(ExptReportVO exptReportVO, Path uploadPath, String zipName, String zipSuffix) {
        // check
        if (BeanUtil.isEmpty(exptReportVO)) {
            return Boolean.FALSE;
        }
        List<ExptGroupReportVO> groupReportList = exptReportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return Boolean.FALSE;
        }

        // 创建压缩文件目录
        Path homePath = Paths.get(LOCAL_ZIP_DIR);
        Path sourceDir = homePath.resolve(zipName);
        Path targetPath = homePath.resolve(zipName + zipSuffix);
        try {
            // 创建文件源目录
            Files.createDirectories(sourceDir);
            // 下载文件到源目录
            download2SourceDir(groupReportList, sourceDir);
            // 压缩源目录为目标文件
            File file = sourceDir.toFile();
            ZipUtil.zip(file);
            // minio 上传文件
            OssInfo ossInfo = ossHelper.upload(targetPath.toFile(), uploadPath.toString(), true);
            // 构建文件返回全路径
            exptReportVO.setZipName(ossInfo.getName());
            exptReportVO.setZipPath(ossHelper.getUrlPath(ossInfo, uploadPath.getParent().toString()));
        } catch (Exception e) {
            log.error("导出pdf报告时, 压缩文件异常");
            return Boolean.FALSE;
        }

        return Boolean.TRUE;
    }

    private void download2SourceDir(List<ExptGroupReportVO> groupReportList, Path sourceDir) {
        for (ExptGroupReportVO groupReport : groupReportList) {
            List<ExptGroupReportVO.ReportFile> reportFiles = groupReport.getReportFiles();
            if (CollUtil.isEmpty(reportFiles)) {
                continue;
            }

            // 确定小组目录
            String groupPathName = (groupReport.getExptGroupNo() == null ? "总报告" : "第" + groupReport.getExptGroupNo() + "组") + File.separator;
            Path groupPath = Paths.get(sourceDir.toString(), groupPathName);
            reportFiles.forEach(reportFile -> {
                try {
                    Files.createDirectories(groupPath);
                    Path filePath = Paths.get(sourceDir.toString(), groupPathName, reportFile.getName());
                    OutputStream outputStream = Files.newOutputStream(filePath);
                    String path = reportFile.getPath();
                    String[] split = path.split("/");
                    ossHelper.download(outputStream, split[split.length - 1]);
                } catch (Exception e) {
                    log.error("压缩文件时：下载文件到源文件数据异常");
                }
            });
        }
    }
}
