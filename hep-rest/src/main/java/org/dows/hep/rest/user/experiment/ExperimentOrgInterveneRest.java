package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgInterveneBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:实验:机构操作-操作指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作-操作指标", description = "机构操作-操作指标")
public class ExperimentOrgInterveneRest {
    private final ExperimentOrgInterveneBiz experimentOrgInterveneBiz;

    /**
    * 心理干预+治疗方案：获取分类+项目
    * @param
    * @return
    */
    @Operation(summary = "心理干预+治疗方案：获取分类+项目")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listOrgInterveneCategs")
    public List<OrgInterveneCategResponse> listOrgInterveneCategs(@RequestBody @Validated FindOrgInterveneCategsRequest findOrgInterveneCategs ) {
        return experimentOrgInterveneBiz.listOrgInterveneCategs(findOrgInterveneCategs);
    }

    /**
    * 饮食干预：保存食谱
    * @param
    * @return
    */
    @Operation(summary = "饮食干预：保存食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveOrgInterveneFood")
    public SaveOrgInterveneFoodResponse saveOrgInterveneFood(@RequestBody @Validated SaveOrgInterveneFoodRequest saveOrgInterveneFood ) {
        return experimentOrgInterveneBiz.saveOrgInterveneFood(saveOrgInterveneFood);
    }

    /**
    * 饮食干预：获取最新食谱
    * @param
    * @return
    */
    @Operation(summary = "饮食干预：获取最新食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getOrgInterveneFood")
    public OrgInterveneFoodResponse getOrgInterveneFood(@RequestBody @Validated FindOrgInterveneFoodRequest findOrgInterveneFood ) {
        return experimentOrgInterveneBiz.getOrgInterveneFood(findOrgInterveneFood);
    }

    /**
    * 运动干预：保存运动方案
    * @param
    * @return
    */
    @Operation(summary = "运动干预：保存运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveOrgInterveneSport")
    public SaveOrgInterveneSportResponse saveOrgInterveneSport(@RequestBody @Validated SaveOrgInterveneSportRequest saveOrgInterveneSport ) {
        return experimentOrgInterveneBiz.saveOrgInterveneSport(saveOrgInterveneSport);
    }

    /**
    * 运动干预：获取最新运动方案
    * @param
    * @return
    */
    @Operation(summary = "运动干预：获取最新运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getOrgInterveneSport")
    public OrgInterveneSportResponse getOrgInterveneSport(@RequestBody @Validated FindOrgInterveneSportRequest findOrgInterveneSport ) {
        return experimentOrgInterveneBiz.getOrgInterveneSport(findOrgInterveneSport);
    }

    /**
    * 心理干预+治疗方案：保存，生成诊疗报告
    * @param
    * @return
    */
    @Operation(summary = "心理干预+治疗方案：保存，生成诊疗报告")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveOrgInterveneTreat")
    public SaveOrgInterveneTreatResponse saveOrgInterveneTreat(@RequestBody @Validated SaveOrgInterveneTreatRequest saveOrgInterveneTreat ) {
        return experimentOrgInterveneBiz.saveOrgInterveneTreat(saveOrgInterveneTreat);
    }

    /**
    * 心理干预+治疗方案：获取最新保存列表
    * @param
    * @return
    */
    @Operation(summary = "心理干预+治疗方案：获取最新保存列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getOrgInterveneTreat")
    public OrgInterveneTreatResponse getOrgInterveneTreat(@RequestBody @Validated FindOrgInterveneTreatRequest findOrgInterveneTreat ) {
        return experimentOrgInterveneBiz.getOrgInterveneTreat(findOrgInterveneTreat);
    }


}