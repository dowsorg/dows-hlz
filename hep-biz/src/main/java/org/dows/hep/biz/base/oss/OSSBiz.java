package org.dows.hep.biz.base.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RequiredArgsConstructor
@Component
public class OSSBiz {

    private final S3OssClient ossClient;

    public OssInfo upload(InputStream is, String fileName) {
        OssInfo info = ossClient.upLoad(is, fileName, false);
        info.setPath("/hepapi/" + fileName);
        return info;
    }

    /**
     * @param os - 输出流
     * @param fileName - 目标文件
     * @date 2023/7/13 14:40
     */
    public void downloadByPath(OutputStream os, String fileName) {
        ossClient.downLoad(os, fileName);
    }

    /**
     * zip.
     *
     * @param attachments 资料附件
     */
    public OssInfo zip(List<MaterialsAttachmentEntity> attachments, String zipName) {
        if (CollUtil.isEmpty(attachments)) {
            return null;
        }

        OssInfo oss;
        File dir = null;
        File zip = null;
        try {
            dir = FileUtil.mkdir(FileUtil.getTmpDirPath() + "/" + IdUtil.fastSimpleUUID());
            for (MaterialsAttachmentEntity attachment : attachments) {
                File file = FileUtil.file(dir, attachment.getFileName());
                ossClient.downLoad(FileUtil.getOutputStream(file), attachment.getFileName());
            }

            zip = ZipUtil.zip(dir.getPath());
            oss = upload(FileUtil.getInputStream(zip), zipName + ".zip");
        } finally {
            FileUtil.del(dir);
            FileUtil.del(zip);
        }

        return oss;
    }


    public String getBase64(String fileName) {
        String file = ossClient.getBasePath();
        if (ossClient.getBasePath().startsWith("/")) {
            file += File.separator + fileName;
        } else {
            file += "/" + fileName;
        }
        String base64 = null;
        try {
            base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get(file)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return base64;
    }

/*    public static void main(String[] args) {
        String base64 = null;
        try {
            base64 = DatatypeConverter.printBase64Binary(Files.readAllBytes(Paths.get("E:\\temps\\1.png")));
            System.out.println(base64);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }*/
}
