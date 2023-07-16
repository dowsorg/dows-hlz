package org.dows.hep.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description oss 属性文件
 * @date 2023/7/15 17:54
 **/
@Component
@ConfigurationProperties(prefix = "oss")
@Data
public class OSSProperties {
    private MinioOss minio;
    @Data
    public static class MinioOss {
        @JsonProperty("bucket-name")
        private String bucketName;

        @JsonProperty("base-path")
        private String basePath;

        @JsonProperty("endpoint")
        private String endpoint;
    }
}
