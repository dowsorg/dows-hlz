package org.dows.hep.biz.orgreport.extracters;

import com.fasterxml.jackson.core.type.TypeReference;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewBaseInfoRsResponse;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewBaseInfoRsBiz;
import org.dows.hep.biz.orgreport.IOrgReportExtracter;
import org.dows.hep.biz.orgreport.OrgReportExtractRequest;
import org.dows.hep.biz.util.ShareBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:37
 */
@Component
public class ViewBaseInfoExtracter implements IOrgReportExtracter<ExperimentIndicatorViewBaseInfoRsResponse> {

    @Autowired
    private ExperimentIndicatorViewBaseInfoRsBiz experimentIndicatorViewBaseInfoRsBiz;

    private static final TypeReference<ExperimentIndicatorViewBaseInfoRsResponse> s_typeRef=new TypeReference<>() {};

    @Override
    public EnumExptOperateType getOperateType() {
        return EnumExptOperateType.VIEWBaseInfo;
    }


    @Override
    public ExperimentIndicatorViewBaseInfoRsResponse getReportData(OrgReportExtractRequest req) throws ExecutionException, InterruptedException {
        return experimentIndicatorViewBaseInfoRsBiz.get(req.getIndicatorFuncId(),req.getExperimentPersonId(),req.getPeriod());
    }

    @Override
    public void accept(ExperimentIndicatorViewBaseInfoRsResponse report, ExptOrgReportNodeDataVO node) {
        node.setViewBaseInfo(report);
    }
}
