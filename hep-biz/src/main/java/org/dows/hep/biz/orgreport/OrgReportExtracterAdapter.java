package org.dows.hep.biz.orgreport;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wuzl
 * @date : 2023/7/18 15:34
 */
@Slf4j
@Component
public class OrgReportExtracterAdapter {
    private static volatile OrgReportExtracterAdapter s_instance;
    public static OrgReportExtracterAdapter Instance(){
        return s_instance;
    }



    private static final ConcurrentHashMap<EnumExptOperateType,IOrgReportExtracter> s_mapExtracters=new ConcurrentHashMap<>();
    private volatile boolean initFlag=false;
    private OrgReportExtracterAdapter(){
        s_instance=this;
    }

    public boolean supportIndicatorCategory(String indicatorCategoryId){
        return null!=getExtracter(indicatorCategoryId);
    }

    public void fillReportData(OrgReportExtractRequest req,ExptOrgReportNodeVO src){
        if(ShareUtil.XObject.anyEmpty(src)){
            return;
        }
        IOrgReportExtracter extracter = getExtracter(src.getIndicatorCategoryId());
        if (null == extracter) {
            return;
        }
        if(ShareUtil.XObject.isEmpty(src.getNodeData() )) {
            src.setNodeData(new ExptOrgReportNodeDataVO());
        }
        extracter.fillReportData(req, src.getNodeData());
    }


    private IOrgReportExtracter getExtracter(String indicatorCategoryId) {
        if (ShareUtil.XObject.isEmpty(indicatorCategoryId)) {
            return null;
        }
        return getExtracter(EnumExptOperateType.ofCategId(indicatorCategoryId));

    }
    private IOrgReportExtracter getExtracter(EnumExptOperateType operateType){
        if(operateType==EnumExptOperateType.NONE){
            return null;
        }
        return ensureMap().get(operateType);
    }
    private Map<EnumExptOperateType,IOrgReportExtracter> ensureMap(){
        if(initFlag){
            return s_mapExtracters;
        }
        try {
            Map<String,IOrgReportExtracter> beans= SpringUtil.getBeansOfType(IOrgReportExtracter.class);
            if(ShareUtil.XObject.isEmpty(beans)){
                log.error("OrgReportExtracterAdapter.ensureMap emptyBeans");
                return s_mapExtracters;
            }
            beans.forEach((k,v)->s_mapExtracters.put(v.getOperateType(), v));
        }catch (Exception ex){
            log.error("OrgReportExtracterAdapter.ensureMap",ex);
            initFlag=false;
            return Collections.emptyMap();
        }
        initFlag=true;
        return s_mapExtracters;
    }
}
