package org.dows.hep.biz.base.oss;

import lombok.RequiredArgsConstructor;
import org.dows.framework.oss.api.OssInfo;
import org.dows.framework.oss.api.S3OssClient;
import org.springframework.stereotype.Component;
import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class OSSBiz {

    private final S3OssClient ossClient;

    public OssInfo upload(InputStream is, String fileName) {
        OssInfo info = ossClient.upLoad(is, fileName, false);
        info.setPath("/hepapi/" + fileName);
        return info;
    }
}
