package org.dows.hep.biz.report;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.properties.PdfServerProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/8 17:02
 **/
@Slf4j
@Component
@Getter
@RequiredArgsConstructor
public class SandReportPdfHelper {

    private final ReportOSSHelper ossHelper;
    private final PdfServerProperties pdfServerProperties;

    public OssInfo convertAndUpload(String exptInstanceId, String exptGroupId, Path uploadPath) {
        return getOssInfo(exptInstanceId, exptGroupId, uploadPath);
    }

    private OssInfo getOssInfo(String exptInstanceId, String exptGroupId, Path uploadPath) {
        OssInfo result = new OssInfo();

        // 获取转换后的文件
        String serverUrl = pdfServerProperties.getServerUrl();
        String appCode = pdfServerProperties.getAppCode();
        String env = pdfServerProperties.getEnv();
        String viewUrl = "";
        if (StrUtil.isNotBlank(exptGroupId)) {
            viewUrl = pdfServerProperties.getSandGroupViewUrl();
        } else {
            viewUrl = pdfServerProperties.getSandExptViewUrl();
        }

        if (StrUtil.isNotBlank(exptInstanceId)) {
            viewUrl += exptInstanceId + "/";
        }
        if (StrUtil.isNotBlank(exptGroupId)) {
            viewUrl += exptGroupId + "/";
        }
        Map<String, Object> param = new HashMap<>();
        param.put("fun", "save");
        param.put("appCode", appCode);
        param.put("url", viewUrl);
        param.put("env", env);
        String resultStr = HttpUtil.get(serverUrl, param);
        if (StrUtil.isBlank(resultStr)) {
            return result;
        }
        JSONObject jsonObject = JSONUtil.parseObj(resultStr);
        String path = (String) jsonObject.get("path");
        if (StrUtil.isBlank(path)) {
            return result;
        }

        result.setPath(path);
        result.setName(uploadPath.getFileName().toString());
        return result;
    }
}
