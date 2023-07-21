package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author fhb
 * @version 1.0
 * @description 上传报告文件，并返回 ossInfo
 * @date 2023/7/13 21:13
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportZipHelper {
    private final ReportOSSHelper ossHelper;
    private static final String ZIP_REPORT_HOME_DIR = SystemConstant.PDF_REPORT_TMP_PATH + "压缩文件" + File.separator;

    /**
     * @param exptReportVO -
     * @return java.util.List<org.dows.framework.oss.api.OssInfo>
     * @author fhb
     * @description
     * @date 2023/7/13 21:33
     */
    public void zipAndUpload(ExptReportVO exptReportVO) {
        if (BeanUtil.isEmpty(exptReportVO)) {
            return;
        }
        List<ExptGroupReportVO> groupReportList = exptReportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return;
        }

        // 创建压缩文件目录
        Path homeDir = Paths.get(ZIP_REPORT_HOME_DIR);
        Path fileName = homeDir.resolve(exptReportVO.getZipName());
        try {
            Path directories = Files.createDirectories(homeDir);
        } catch (IOException e) {
            throw new BizException("导出pdf报告时, 创建临时压缩文件目录异常");
        }
        try (OutputStream out = Files.newOutputStream(fileName);
             ZipOutputStream zos = new ZipOutputStream(out)) {
            // 压缩文件
            toZipExpt(exptReportVO, zos);
            // minio 上传文件
            OssInfo ossInfo = ossHelper.upload(fileName.toFile(), fileName.toString(), true);
            // 构建文件返回全路径
            exptReportVO.setZipName(ossInfo.getName());
            exptReportVO.setZipPath(ossHelper.getUrlPath(ossInfo));
        } catch (IOException e) {
            throw new BizException("导出pdf报告时, 压缩文件或文件上传异常");
        }
        try {
            // 如果上传 upload 慢,则删不掉, 因为有程序占用了
            Files.deleteIfExists(fileName);
            // 如果多线程则删不掉, 别的文件又加进来了, 不可删除非空的目录, 怎么办呢亲
            Files.deleteIfExists(homeDir);
        } catch (IOException e) {
            log.error("导出pdf报告时, 删除pdf报告的本地文件异常");
        }
    }

    private void toZipExpt(ExptReportVO reportVO, ZipOutputStream zos) {
        byte[] buf = new byte[1024];
        try {
            List<ExptGroupReportVO> groupReportList = reportVO.getGroupReportList();
            for (ExptGroupReportVO groupReportVO : groupReportList) {
                // 添加 zipEntry
                Integer exptGroupNo = groupReportVO.getExptGroupNo();
                String groupDirName = "";
                if (exptGroupNo != null) {
                    groupDirName = "第" + exptGroupNo + "组" + File.separator;
                } else {
                    groupDirName = "总报告" + File.separator;
                }

                //
                List<ExptGroupReportVO.ReportFile> paths = groupReportVO.getPaths();
                for (ExptGroupReportVO.ReportFile path : paths) {
                    Path temFilePath = Paths.get(ZIP_REPORT_HOME_DIR, path.getName());
                    OutputStream out = Files.newOutputStream(temFilePath);
                    ossHelper.download(out, path.getName());

                    zos.putNextEntry(new ZipEntry(groupDirName + temFilePath.getFileName()));

                    int len;
                    InputStream in = Files.newInputStream(temFilePath);
                    while ((len = in.read(buf)) != -1) {
                        zos.write(buf, 0, len);
                    }

                    zos.closeEntry();
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e1) {
            throw new RuntimeException("zip error: ", e1);
        }
    }
}
