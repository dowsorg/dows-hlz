package org.dows.hep.biz.orgreport;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorFuncRsResponse;
import org.dows.hep.api.base.indicator.response.ExperimentOrgModuleRsResponse;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.user.experiment.response.ExptOrgFlowReportResponse;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.biz.base.indicator.ExperimentOrgModuleBiz;
import org.dows.hep.biz.util.ExptOrgFlowValidator;
import org.dows.hep.biz.util.ExptRequestValidator;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentOrgEntity;
import org.dows.hep.entity.OperateFlowEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author : wuzl
 * @date : 2023/7/18 15:59
 */

@Component
@RequiredArgsConstructor
public class OrgReportComposer {

    private final OrgReportExtracterAdapter orgReportExtracterAdapter;
    private final ExperimentOrgModuleBiz experimentOrgModuleBiz;

    public OrgReportExtractRequest createRequest(ExptRequestValidator validator){
        return new OrgReportExtractRequest()
                .setAppId(validator.getAppId())
                .setExperimentInstanceId(validator.getExperimentInstanceId())
                .setExperimentGroupId(validator.getExperimentGroupId())
                .setExperimentOrgId(validator.getExperimentOrgId())
                .setExperimentPersonId(validator.getExperimentPersonId())
                .setIndicatorFuncId(validator.getIndicatorFuncId())
                ;
    }

    public ExptOrgFlowReportResponse composeReport(ExptRequestValidator exptValidator,ExptOrgFlowValidator flowValidator,ExptOrgReportNodeVO newNode){
        final ExperimentOrgEntity exptOrg= exptValidator.getExperimentOrg();
        if(null==flowValidator){
            flowValidator=ExptOrgFlowValidator.create(exptValidator);
        }
        OperateFlowEntity rowFlow=flowValidator.getOrgFlow(true,
                        OperateFlowEntity::getOperateFlowId,
                        OperateFlowEntity::getPeriods,
                        OperateFlowEntity::getOperateTime,
                        OperateFlowEntity::getOperateGameDay
                        );
        ExptOrgFlowReportResponse rst=new ExptOrgFlowReportResponse()
                .setOperateFlowId(rowFlow.getOperateFlowId())
                .setOperateTime(rowFlow.getOperateTime())
                .setOperateGameDay(rowFlow.getOperateGameDay())
                .setReportName(String.format("%s报告",exptOrg.getExperimentOrgName()))
                .setNodes(new ArrayList<>());
        List<ExperimentOrgModuleRsResponse> modules=experimentOrgModuleBiz.getByExperimentOrgIdAndExperimentPersonId(exptValidator.getExperimentOrgId());
        if(ShareUtil.XObject.isEmpty(modules)){
            return rst;
        }

        final List<ExperimentIndicatorFuncRsResponse> funcs=new ArrayList<>();
        modules.forEach(module->{
            if(ShareUtil.XObject.isEmpty(module.getExperimentIndicatorFuncRsResponseList())){
                return;
            }
            module.getExperimentIndicatorFuncRsResponseList().forEach(func->{
                if(!orgReportExtracterAdapter.supportIndicatorCategory(func.getIndicatorCategoryId())){
                    return;
                }
                if(null!=newNode &&func.getIndicatorCategoryId().equals(newNode.getIndicatorCategoryId()) ){
                    return;
                }
                funcs.add(func);
            });
        });
        if(funcs.size()==0){
            return rst;
        }
        CompletableFuture[] futures=new CompletableFuture[funcs.size()];
        int i=0;
        final List<ExptOrgReportNodeVO> nodes=rst.getNodes();
        for(ExperimentIndicatorFuncRsResponse func:funcs){
            futures[i++]=CompletableFuture.runAsync(()->{
                ExptOrgReportNodeVO node=new ExptOrgReportNodeVO()
                        .setIndicatorCategoryId(func.getIndicatorCategoryId())
                        .setIndicatorFuncId(func.getIndicatorFuncId())
                        .setIndicatorFuncName(func.getIndicatorFuncName());
                nodes.add(node);
                OrgReportExtractRequest req=createRequest(exptValidator).setIndicatorFuncId(func.getIndicatorFuncId());
                orgReportExtracterAdapter.fillReportData(req,node);
            });
        }
        CompletableFuture.allOf(futures).join();
        if(null!=newNode) {
            nodes.add(newNode);
        }
        nodes.sort(Comparator.comparing(item-> EnumExptOperateType.ofCategId(item.getIndicatorCategoryId()).getReportFlowSeq()));
        return rst;
    }
}
