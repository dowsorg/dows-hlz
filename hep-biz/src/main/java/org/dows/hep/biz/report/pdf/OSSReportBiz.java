package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.minio.MinioOssClient;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.List;
import java.util.Map;
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
    private final MinioOssClient ossClient;
    private static final String URL_PATH_SEPARATOR = "/";
    private static final String ZIP_REPORT_HOME_DIR = SystemConstant.PDF_REPORT_TMP_PATH + "压缩文件" + File.separator;

    /**
     * @param exptReportVO -
     * @return java.util.List<org.dows.framework.oss.api.OssInfo>
     * @author fhb
     * @description
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

        // 创建压缩文件目录
        File homeDir = new File(ZIP_REPORT_HOME_DIR);
        boolean mkdirs = homeDir.mkdirs();
        // 文件名
        String zipName = exptReportVO.getZipName();
        File file = new File(homeDir, zipName);
        FileOutputStream out = null;
        ZipOutputStream zos = null;
        try {
            out = new FileOutputStream(file);
            zos = new ZipOutputStream(out);
        } catch (FileNotFoundException e) {
            log.error("导出报告pdf数据时,获取压缩包文件异常");
        }
        if (groupReportList.size() > 0) {
            toZipExpt(exptReportVO, zos);
        }

        // 上传压缩文件
        OssInfo ossInfo = ossClient.upLoad(file, file.getName(), true);

        ClassPathResource classPathResource = new ClassPathResource("application-hep-oss.yml");
        ByteArrayInputStream inputStream = IoUtil.toStream(classPathResource.readBytes());

        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(inputStream);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 获取配置数据
        Map oss = (Map) data.get("oss");
        Map minio = (Map) oss.get("minio");
        String endpoint = (String) minio.get("endpoint");
        String bucketName = (String) minio.get("bucket-name");
        String basePath = (String) minio.get("base-path");

        exptReportVO.setZipName(ossInfo.getName());
        exptReportVO.setZipPath(endpoint + URL_PATH_SEPARATOR + bucketName + URL_PATH_SEPARATOR + basePath + URL_PATH_SEPARATOR + ossInfo.getName());
    }

    private void toZipExpt(ExptReportVO reportVO, ZipOutputStream zos ) {
        byte[] buf = new byte[1024];
        try {
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
        // 有组则分组
        Integer exptGroupNo = groupReportVO.getExptGroupNo();
        String groupDirName = "";
        if (exptGroupNo != null) {
            groupDirName = "第" + exptGroupNo + "组" + File.separator;
        } else {
            groupDirName = "总报告" + File.separator;
        }

        List<ExptGroupReportVO.ReportFile> paths = groupReportVO.getPaths();
        for (ExptGroupReportVO.ReportFile path : paths) {
            File file = new File(path.getPath());
            zos.putNextEntry(new ZipEntry(groupDirName  + file.getName()));
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
