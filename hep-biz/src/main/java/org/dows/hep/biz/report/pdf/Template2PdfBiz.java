package org.dows.hep.biz.report.pdf;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.layout.font.FontProvider;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.minio.MinioOssClient;
import org.dows.hep.vo.report.ExptReportModel;
import org.dows.hep.vo.report.ExptSchemeReportModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fhb
 * @version 1.0
 * @description 模板技术
 * @date 2023/7/6 14:00
 **/
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class Template2PdfBiz {
    private final MinioOssClient ossClient;
    private final FreeMarkerConfig freeMarkerConfig;
    private final Map<String, Template> templateCache = new HashMap<>(3);
    private final ConverterProperties converterProperties = new ConverterProperties();

    @PostConstruct
    private void init() throws IOException {
        // convertProperties
        FontProvider fontProvider = new FontProvider();
        fontProvider.addFont(FontProgramFactory.createFont(IOUtils.toByteArray(new ClassPathResource("pdf/fonts/simhei.ttf").getInputStream())));
        fontProvider.addFont(FontProgramFactory.createFont(IOUtils.toByteArray(new ClassPathResource("pdf/fonts/simhei-bold.ttf").getInputStream())));
        converterProperties.setFontProvider(fontProvider);
    }

    /**
     * @param pdfVO - model
     * @param schemeFlt - view
     * @param filePath - target
     * @return org.dows.framework.oss.api.OssInfo
     * @author fhb
     * @description 将 model 填充进 view 中, 生成 pdf 文件并上传
     * @date 2023/7/20 11:54
     */
    public OssInfo convertAndUpload(ExptSchemeReportModel pdfVO, String schemeFlt, Path filePath) {
        // convert
        Path homeDir = filePath.getParent();
        File targetFile = filePath.toFile();
        try {
            Path directories = Files.createDirectories(homeDir);
            convert2Pdf(pdfVO, schemeFlt, targetFile);
        } catch (IOException | TemplateException e) {
            log.error("导出方报告时，html转pdf异常");
            throw new BizException("导出报告时，html转pdf异常");
        }

        // 上传
        OssInfo ossInfo = ossClient.upLoad(targetFile, targetFile.getName(), true);

        // 删除本地文件
        try {
            // todo 多线程情况如何优化
            Files.deleteIfExists(filePath);
            // 多线程目录删不掉
//            Files.deleteIfExists(homeDir);
        } catch (IOException e) {
            log.error("导出报告时，删除本地临时文件异常");
        }

        return ossInfo;
    }

    private void convert2Pdf(ExptReportModel pdfData, String ftlName, File targetFile) throws IOException, TemplateException {
        Template template = templateCache.get(ftlName);
        if (template == null) {
            template = freeMarkerConfig.getConfiguration().getTemplate(ftlName);
            templateCache.put(ftlName, template);
        }

        StringWriter out = new StringWriter();
        template.process(pdfData, out);
        out.flush();
        String htmlString = out.toString();
        out.close();

        HtmlConverter.convertToPdf(htmlString, Files.newOutputStream(targetFile.toPath()), converterProperties);
    }
}
