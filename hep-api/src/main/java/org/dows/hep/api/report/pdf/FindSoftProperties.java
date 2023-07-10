package org.dows.hep.api.report.pdf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/7/6 14:39
 **/
@Component
@ConfigurationProperties(prefix = "findsoft")
@Data
public class FindSoftProperties {

    private String name = "软件名称";
    private String version = "1.0.0";
    private String copyRight = "©  &CYear findsoft 上海哲寻信息科技有限公司";
    private String abbreviation = "软件简称";
    private boolean scoreAudit = false;

    // logo 类路径
    private String findsoftLogo = "pdf/images/findsoft-logo.jpg";
    // 封面 类路径
    private String cover = "pdf/images/cover.png";
    // logo 类路径
    private String logo = "pdf/images/logo.png";

    // 总报告标题
    private String exptOverviewReportTitle = "";
    // 方案设计标题
    private String exptSchemeReportTitle = "";
    // 沙盘标题
    private String exptSandReportTitle = "";

    // 总预览模板路径
    private String exptOverviewFtl = "pdf/templates/experiment-overview.ftl";
    // 方案设计模板路径
    private String exptSchemeFtl = "pdf/templates/experiment-scheme.ftl";
    // 沙盘模板路径
    private String exptSandFtl = "pdf/templates/experiment-sand.ftl";


}
