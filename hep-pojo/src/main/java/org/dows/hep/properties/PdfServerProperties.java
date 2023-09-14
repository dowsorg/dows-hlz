package org.dows.hep.properties;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("server-url")
    private String serverUrl;

    // 业务代码
    @JsonProperty("app-code")
    private String appCode;

    // 环境
    private String env;

    // 前端页面地址
    @JsonProperty("sand-group-view-url")
    private String sandGroupViewUrl;

    @JsonProperty("sand-expt-view-url")
    private String sandExptViewUrl;

    @JsonProperty("scheme-group-view-url")
    private String schemeGroupViewUrl;

    @JsonProperty("scheme-expt-view-url")
    private String schemeExptViewUrl;
}
