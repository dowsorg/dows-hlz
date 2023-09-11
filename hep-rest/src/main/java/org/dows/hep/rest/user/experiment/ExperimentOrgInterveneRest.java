package org.dows.hep.rest.user.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.annotation.Resubmit;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.ExptSportPlanResponse;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;
import org.dows.hep.api.user.experiment.response.SaveExptInterveneResponse;
import org.dows.hep.api.user.experiment.response.SaveExptTreatResponse;
import org.dows.hep.biz.user.experiment.ExperimentOrgInterveneBiz;
import org.dows.hep.biz.vo.CalcExptFoodCookbookResult;
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
    public List<Categ4ExptVO> listInterveneCateg4Expt(@RequestBody @Validated FindInterveneCateg4ExptRequest findCateg ) throws JsonProcessingException {
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

    @Operation(summary = "运动干预：获取数据库运动项目列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/pageSportItem4Expt")
    public Page<SportItemResponse> pageSportItem4Expt(@RequestBody @Validated FindInterveneList4ExptRequest findSport ){
        return experimentOrgInterveneBiz.pageSportItem4Expt(findSport);
    }

    @Operation(summary = "运动干预：获取分类+项目")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listSportCateg4Expt")
    public List<Categ4ExptVO> listSportCateg4Expt(@RequestBody @Validated FindInterveneCateg4ExptRequest findSport ) throws JsonProcessingException{
        return experimentOrgInterveneBiz.listSportCateg4Expt(findSport);
    }

    @Operation(summary = "治疗干预：获取分类+项目")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listTreatCateg4Expt")
    public List<Categ4ExptVO> listTreatCateg4Expt(@RequestBody @Validated FindInterveneCateg4ExptRequest findTreat ) throws JsonProcessingException {
        return experimentOrgInterveneBiz.listTreatCateg4Expt(findTreat);
    }

    @Operation(summary = "治疗干预：按多id获取数据库项目列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/listTreatItem4Expt")
    public List<TreatItemResponse> listTreatItem4Expt(@RequestBody @Validated FindTreatList4ExptRequest findTreat ){
        return experimentOrgInterveneBiz.listTreatItem4Expt(findTreat);
    }
    //endregion


    //region 实验数据读写

    @Operation(summary = "饮食干预：获取学生最新食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptFoodCookbook")
    public CalcExptFoodCookbookResult getExptFoodCookbook(@RequestBody @Validated ExptOperateOrgFuncRequest exptOperate ) {
        return experimentOrgInterveneBiz.getExptFoodCookbook(exptOperate);
    }


    @Operation(summary = "饮食干预：保存学生食谱")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptFoodCookbook")
    public SaveExptInterveneResponse saveExptFoodCookbook(@RequestBody @Validated SaveExptFoodRequest saveFood , HttpServletRequest request ) {
        return experimentOrgInterveneBiz.saveExptFoodCookbook(saveFood, request);
    }

    @Operation(summary = "饮食干预：计算营养统计，膳食宝塔")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/calcExptFoodGraph")
    public CalcExptFoodCookbookResult calcExptFoodGraph(@RequestBody @Validated CalcExptFoodGraphRequest calcFood ) {
        return experimentOrgInterveneBiz.calcExptFoodGraph(calcFood);
    }

    @Operation(summary = "运动干预：获取学生最新运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptSportPlan")
    public ExptSportPlanResponse getExptSportPlan(@RequestBody @Validated ExptOperateOrgFuncRequest exptOperate ) {
        return experimentOrgInterveneBiz.getExptSportPlan(exptOperate);
    }

    @Operation(summary = "运动干预：保存学生运动方案")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptSportPlan")
    public SaveExptInterveneResponse saveExptSportPlan(@RequestBody @Validated SaveExptSportRequest saveSport, HttpServletRequest request ) {
        return experimentOrgInterveneBiz.saveExptSportPlan(saveSport,request);
    }

    @Operation(summary = "治疗干预：获取学生最新保存项目列表")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/getExptTreatPlan")
    public ExptTreatPlanResponse getExptTreatPlan(@RequestBody @Validated ExptOperateOrgFuncRequest exptOperate){
        return experimentOrgInterveneBiz.getExptTreatPlan(exptOperate);
    }

    @Resubmit(duration = 3)
    @Operation(summary = "治疗干预：保存治疗方案，生成治疗报告")
    @PostMapping("v1/userExperiment/experimentOrgIntervene/saveExptTreatPlan")
    public SaveExptTreatResponse saveExptTreatPlan(@RequestBody @Validated SaveExptTreatRequest saveTreat, HttpServletRequest request){
        return experimentOrgInterveneBiz.saveExptTreatPlan(saveTreat,request);
    }
    //endregion


}