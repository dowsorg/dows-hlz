package org.dows.hep.biz.snapshot;

import jakarta.servlet.http.HttpServletRequest;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author : wuzl
 * @date : 2023/7/2 19:21
 */
public class SnapshotRequestHolder {

    public static final String ATTRKeyAppId="APPID";
    public static final String ATTRKeyExperimentId="EXPERIMENTID";
    public static void setSnapshotRequest(String appId,String experimentId){
        HttpServletRequest request=getHttpRequest();
        request.setAttribute(ATTRKeyAppId,appId);
        experimentId=ShareUtil.XString.defaultIfEmpty(experimentId, "");
        request.setAttribute(ATTRKeyExperimentId,experimentId);
    }
    public static boolean hasSnapshotRequest(){
        HttpServletRequest request=getHttpRequest();
        return null!=request&&ShareUtil.XObject.notEmpty(request.getAttribute(ATTRKeyExperimentId));
    }

    public static String getRefExperimentId(EnumSnapshotType snapshotType){
        if(!hasSnapshotRequest()){
            return null;
        }
        HttpServletRequest request=getHttpRequest();
        String refExperimentId=(String)request.getAttribute(snapshotType.getCode());
        if(ShareUtil.XObject.notEmpty(refExperimentId)){
            return refExperimentId;
        }
        final String appId=(String)request.getAttribute(ATTRKeyAppId);
        final String experimentId=(String)request.getAttribute(ATTRKeyExperimentId);
        refExperimentId=SnapshotRefCache.Instance().getRefExperimentId(appId,snapshotType,experimentId);
        if(ShareUtil.XObject.notEmpty(refExperimentId)){
            request.setAttribute(snapshotType.getCode(),refExperimentId);
        }
        return refExperimentId;
    }


    public static HttpServletRequest getHttpRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes != null ? requestAttributes.getRequest() : null;
    }
}
