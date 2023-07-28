package org.dows.hep.biz.report;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.minio.MinioOssClient;
import org.dows.hep.properties.OSSProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.OutputStream;

/**
 * @author fhb
 * @version 1.0
 * @description 为报告提供 oss 服务, 解耦出来, 以防 oss 服务变更
 * @date 2023/7/21 9:57
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ReportOSSHelper {

    private final MinioOssClient ossClient;
    private final OSSProperties ossProperties;
    private static final String URL_PATH_SEPARATOR = "/";

    /**
     * @param file       - 要上传的文件
     * @param targetName - 目标文件命名
     * @param isOverride - 同名是否覆盖
     * @return org.dows.framework.oss.api.OssInfo
     * @author fhb
     * @description 上传文件
     * @date 2023/7/21 9:59
     */
    public OssInfo upload(File file, String targetName, boolean isOverride) {
        return ossClient.upLoad(file, targetName, isOverride);
    }

    /**
     * @param out        - 输出流
     * @param targetName - 目标文件名
     * @author fhb
     * @description 下载文件
     * @date 2023/7/21 10:06
     */
    public void download(OutputStream out, String targetName) {
        ossClient.downLoad(out, targetName);
    }

    /**
     * @param ossInfo - 获取文件完整 uri 路径
     * @return java.lang.String
     * @author fhb
     * @description 获取文件完整 uri 路径
     * @date 2023/7/20 15:23
     */
    public String getUrlPath(OssInfo ossInfo) {
        OSSProperties.MinioOss minio = ossProperties.getMinio();
        String endpoint = minio.getEndpoint();
        String basePath = minio.getBasePath();
        String bucketName = minio.getBucketName();
        // 构建返回值
        return endpoint
                + URL_PATH_SEPARATOR
                + bucketName
                + URL_PATH_SEPARATOR
                + basePath
                + URL_PATH_SEPARATOR
                + ossInfo.getName();
    }
}
