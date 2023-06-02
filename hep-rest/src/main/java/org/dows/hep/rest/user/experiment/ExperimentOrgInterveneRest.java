package org.dows.hep.rest.user.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.user.experiment.ExperimentOrgInterveneBiz;
import org.dows.hep.biz.vo.Categ4ExptVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:实验:机构操作-操作指标
* @folder user-hep/机构操作-操作指标
 *
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "机构操作-操作指标", description = "机构操作-操作指标")
public class ExperimentOrgInterveneRest {
    private final ExperimentOrgInterveneBiz experimentOrgInterveneBiz;


    //region 快照数据查询
    @Operation(summary = "获取干预类别：获取数据库食谱，菜肴，食材，运动项目等类别")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listInterveneCateg4Expt")
    public List<Categ4ExptVO> listInterveneCateg4Expt(@RequestBody @Validated FindInterveneCateg4ExptRequest findCateg ) {
        return experimentOrgInterveneBiz.listInterveneCateg4Expt(findCateg);
    }

    @Operation(summary = "饮食干预：获取数据库食谱列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/pageFoodCookbook4Expt")
    public Page<FoodCookBookResponse> pageFoodCookbook4Expt(@RequestBody @Validated FindInterveneList4ExptRequest findFood ) {
        return experimentOrgInterveneBiz.pageFoodCookbook4Expt(findFood);
    }

    @Operation(summary = "饮食干预：查看数据库食谱详情")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getFoodCookbook4Expt")
    public FoodCookBookInfoResponse getFoodCookbook4Expt(@RequestBody @Validated GetInfo4ExptRequest getInfo) {
        return experimentOrgInterveneBiz.getFoodCookbook4Expt(getInfo);
    }

    @Operation(summary = "饮食干预：获取数据库菜肴列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/pageFoodDishes4Expt")
    public Page<FoodDishesResponse> pageFoodDishes4Expt(@RequestBody @Validated FindInterveneList4ExptRequest findFood ) {
        return experimentOrgInterveneBiz.pageFoodDishes4Expt(findFood);
    }

    @Operation(summary = "饮食干预：获取数据库食材列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/pageFoodMaterial4Expt")
    public Page<FoodMaterialResponse> pageFoodMaterial4Expt(@RequestBody @Validated FindInterveneList4ExptRequest findFood ) {
        return experimentOrgInterveneBiz.pageFoodMaterial4Expt(findFood);
    }

    @Operation(summary = "运动干预：获取数据库运动方案列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/pageSportPlan4Expt")
    public Page<SportPlanResponse> pageSportPlan4Expt(@RequestBody @Validated FindInterveneList4ExptRequest findSport ){
        return experimentOrgInterveneBiz.pageSportPlan4Expt(findSport);
    }

    @Operation(summary = "运动干预：获取数据库运动方案详情")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getSportPlan4Expt")
    public SportPlanInfoResponse getSportPlan4Expt(@RequestBody @Validated GetInfo4ExptRequest getInfo) {
        return experimentOrgInterveneBiz.getSportPlan4Expt(getInfo);
    }

    @Operation(summary = "治疗干预：获取分类+项目")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listTreatCateg4Expt")
    public List<Categ4ExptVO> listTreatCateg4Expt(@RequestBody @Validated FindInterveneCateg4ExptRequest findTreat ){
        return experimentOrgInterveneBiz.listTreatCateg4Expt(findTreat);
    }

    @Operation(summary = "治疗干预：按多id获取数据库项目列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listTreatItem4Expt")
    public ExptTreatPlanResponse listTreatItem4Expt(@RequestBody @Validated FindTreatList4ExptRequest findTreat ){
        return experimentOrgInterveneBiz.listTreatItem4Expt(findTreat);
    }
    //endregion


    //region 实验数据读写

    @Operation(summary = "饮食干预：获取学生最新食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptFoodCookbook")
    public ExptFoodCookbookResponse getExptFoodCookbook(@RequestBody @Validated ExptOrgFuncRequest exptOrgFunc ) {
        return experimentOrgInterveneBiz.getExptFoodCookbook(exptOrgFunc);
    }


    @Operation(summary = "饮食干预：保存学生食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptFoodCookbook")
    public SaveExptInterveneResponse saveExptFoodCookbook(@RequestBody @Validated SaveExptFoodRequest saveFood ) {
        return experimentOrgInterveneBiz.saveExptFoodCookbook(saveFood);
    }

    @Operation(summary = "饮食干预：计算营养统计，膳食宝塔")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/calcExptFoodGraph")
    public ExptFoodGraphResponse calcExptFoodGraph(@RequestBody @Validated SaveExptFoodRequest saveFood ) {
        return experimentOrgInterveneBiz.calcExptFoodGraph(saveFood);
    }

    @Operation(summary = "运动干预：获取学生最新运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptSportPlan")
    public ExptSportPlanResponse getExptSportPlan(@RequestBody @Validated ExptOrgFuncRequest exptOrgFunc ) {
        return experimentOrgInterveneBiz.getExptSportPlan(exptOrgFunc);
    }

    @Operation(summary = "运动干预：保存学生运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptSportPlan")
    public SaveExptInterveneResponse saveExptSportPlan(@RequestBody @Validated SaveExptSportRequest saveSport ) {
        return experimentOrgInterveneBiz.saveExptSportPlan(saveSport);
    }

    @Operation(summary = "治疗干预：获取学生最新保存项目列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptTreatPlan")
    public ExptTreatPlanResponse getExptTreatPlan(@RequestBody @Validated ExptOrgFuncRequest exptOrgFunc){
        return experimentOrgInterveneBiz.getExptTreatPlan(exptOrgFunc);
    }

    @Operation(summary = "治疗干预：保存治疗方案，生成治疗报告")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptTreatPlan")
    public SaveExptTreatResponse saveExptTreatPlan(@RequestBody @Validated SaveExptTreatRequest saveTreat){
        return experimentOrgInterveneBiz.saveExptTreatPlan(saveTreat);
    }
    //endregion


}