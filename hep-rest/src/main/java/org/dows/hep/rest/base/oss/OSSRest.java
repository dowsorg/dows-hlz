package org.dows.hep.rest.base.oss;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.biz.base.oss.OSSBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Tag(name = "OSS文件上传")
@RequiredArgsConstructor
@RestController
public class OSSRest {

    private final OSSBiz ossBiz;

    @Operation(summary = "oss文件上传")
    @PostMapping("/v1/file/upload")
    OssInfo uploadFile(@RequestParam(value = "file") MultipartFile file) throws IOException{
        if (Objects.isNull(file) || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }
//        if (file.getSize() > 629145600L) {
//            throw new IOException("文件超过最大限制6M");
//        }

        // 文件名后缀
        String suffix = null;
        String originalName = "";
        String originalFilename = file.getOriginalFilename();
        if (StrUtil.isNotBlank(originalFilename)) {
            assert originalFilename != null;
            suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            originalName = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        }
        // 文件名
        String timeMillis = String.valueOf(System.currentTimeMillis());
        String fileName = originalName + "-" + timeMillis + suffix;

        // 输入流
        OssInfo info = null;
        InputStream is = null;
        try {
            is = file.getInputStream();
            info= ossBiz.upload(is, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            assert is != null;
            is.close();
        }

        return info;
    }
}
