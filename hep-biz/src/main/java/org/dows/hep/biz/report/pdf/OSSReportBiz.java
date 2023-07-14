package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.local.LocalOssClient;
import org.dows.hep.api.report.pdf.ExptGroupReportVO;
import org.dows.hep.api.report.pdf.ExptReportVO;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class OSSReportBiz {
//    private final MinioOssClient ossClient;
    private final LocalOssClient ossClient;
//    private final S3OssClient ossClient;


    /**
     * @param exptReportVO -
     * @return java.util.List<org.dows.framework.oss.api.OssInfo>
     * @author fhb
     * @description todo 改造批量方法
     * @date 2023/7/13 21:33
     */
    public void upload(ExptReportVO exptReportVO) {
        if (BeanUtil.isEmpty(exptReportVO)) {
            return;
        }
        List<ExptGroupReportVO> groupReportList = exptReportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return;
        }

        if (groupReportList.size() > 1) {
            toZipExpt(exptReportVO);
        } else {
            toZipGroup(exptReportVO);
        }

        String zipPath = exptReportVO.getZipPath();
        String zipName = exptReportVO.getZipName();
        OssInfo ossInfo = ossClient.upLoad(new File(zipPath), zipName, true);
        exptReportVO.setZipPath(ossInfo.getPath());
        exptReportVO.setZipName(ossInfo.getName());
    }

    private void toZipGroup(ExptReportVO reportVO) {
        List<ExptGroupReportVO> groupReportList = reportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return;
        }

        byte[] buf = new byte[1024];
        ZipOutputStream zos = null;
        try {
            FileOutputStream out = new FileOutputStream(reportVO.getZipPath());
            zos = new ZipOutputStream(out);
            ExptGroupReportVO groupReportVO = groupReportList.get(0);
            doZip(buf, zos, groupReportVO);
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
    }

    private void toZipExpt(ExptReportVO reportVO) {
        byte[] buf = new byte[1024];
        ZipOutputStream zos = null;
        try {
            FileOutputStream out = new FileOutputStream(reportVO.getZipPath());
            zos = new ZipOutputStream(out);

            List<ExptGroupReportVO> groupReportList = reportVO.getGroupReportList();
            for (ExptGroupReportVO groupReportVO : groupReportList) {
                doZip(buf, zos, groupReportVO);
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
    }

    private static void doZip(byte[] buf, ZipOutputStream zos, ExptGroupReportVO groupReportVO) throws IOException {
        Integer exptGroupNo = groupReportVO.getExptGroupNo();
        String groupDirName = "第" + exptGroupNo + "组";
        List<ExptGroupReportVO.ReportFile> paths = groupReportVO.getPaths();

        for (ExptGroupReportVO.ReportFile path : paths) {
            File file = new File(path.getPath());
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
}
