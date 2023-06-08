package org.dows.hep.biz.base.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.RequiredArgsConstructor;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.api.S3OssClient;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
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
}
