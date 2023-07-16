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
import org.dows.hep.vo.report.ExptReportModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/7/6 14:00
 **/
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class Template2PdfBiz {
    private final FreeMarkerConfig freeMarkerConfig;
    private final Map<String, Template> templateCache = new HashMap<>(3);
    private final ConverterProperties converterProperties = new ConverterProperties();

    @PostConstruct
    private void init() throws IOException {
        // convertProperties
        FontProvider fontProvider = new FontProvider();
        // 黑体
        fontProvider.addFont(FontProgramFactory.createFont(IOUtils.toByteArray(new ClassPathResource("pdf/fonts/simhei.ttf").getInputStream())));
        fontProvider.addFont(FontProgramFactory.createFont(IOUtils.toByteArray(new ClassPathResource("pdf/fonts/simhei-bold.ttf").getInputStream())));
        converterProperties.setFontProvider(fontProvider);
    }

    public void convert2Pdf(ExptReportModel pdfData, String ftlName, File targetFile) throws IOException, TemplateException {
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
