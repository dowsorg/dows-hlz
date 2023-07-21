package org.dows.hep.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description findsoft 属性文件
 * @date 2023/7/6 14:39
 **/
@Component
@ConfigurationProperties(prefix = "findsoft")
@Data
public class FindSoftProperties {
    // base info
    private String name = "";
    private String version = "";
    private String copyRight = "";
    private String abbreviation = "";
    private boolean scoreAudit;
    // logo 类路径
    private String findsoftLogo = "";
    // 封面 类路径
    private String cover = "";
    // logo 类路径
    private String logo = "";
    // 总报告标题
    private String exptOverviewReportTitle = "";
    // 方案设计标题
    private String exptSchemeReportTitle = "";
    // 沙盘标题
    private String exptSandReportTitle = "";
    // 总预览模板路径
    private String exptOverviewFtl = "";
    // 方案设计模板路径
    private String exptSchemeFtl = "";
    // 沙盘模板路径
    private String exptSandFtl = "";
}
