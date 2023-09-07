package org.dows.hep.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description pdf服务器属性文件
 * @date 2023/9/7 15:35
 **/
@Component
@ConfigurationProperties(prefix = "pdf-server")
@Data
public class PdfServerProperties {
    // 服务器地址
    private String url;

    // 命名空间
    private String namespace;
}
