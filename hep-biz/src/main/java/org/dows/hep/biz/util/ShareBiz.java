package org.dows.hep.biz.util;

/**
 * 业务规则公共类
 *
 * @author : wuzl
 * @date : 2023/5/4 15:37
 */
public class ShareBiz {
    public static String ensureCategPathSuffix(String src){
        if(ShareUtil.XObject.isEmpty(src)){
            return src;
        }
        return ShareUtil.XString.eusureEndsWith(src,"/");
    }
}
