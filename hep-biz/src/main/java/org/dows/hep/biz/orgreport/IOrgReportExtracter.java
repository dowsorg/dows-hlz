package org.dows.hep.biz.orgreport;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.util.ShareUtil;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:28
 */
public interface IOrgReportExtracter<T> extends IOrgReportConsumer<T> {
    EnumExptOperateType getOperateType();

    TypeReference<T> getReportClass();

    T getReportData(OrgReportExtractRequest req);

    default void fillReportData(OrgReportExtractRequest req, ExptOrgReportNodeDataVO node){
        if(ShareUtil.XObject.isEmpty(node)){
            return;
        }
        accept(getReportData(req), node);
    }
}
