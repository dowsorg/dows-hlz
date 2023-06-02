package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.biz.vo.Categ4ExptVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @description project descr:实验:机构操作-操作指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
public class ExperimentOrgInterveneBiz{

    //region 快照数据查询
    public List<Categ4ExptVO> listInterveneCateg4Expt(FindInterveneCateg4ExptRequest findCateg ) {
        return null;
    }

    public Page<FoodCookBookResponse> pageFoodCookbook4Expt(FindInterveneList4ExptRequest findFood ){
        return null;
    }
    public FoodCookBookInfoResponse getFoodCookbook4Expt(GetInfo4ExptRequest getInfo) {
        return null;
    }
    public Page<FoodDishesResponse> pageFoodDishes4Expt(FindInterveneList4ExptRequest findFood ) {
        return null;
    }
    public Page<FoodMaterialResponse> pageFoodMaterial4Expt( FindInterveneList4ExptRequest findFood ) {
        return null;
    }
    public Page<SportPlanResponse> pageSportPlan4Expt(FindInterveneList4ExptRequest findSport ){
        return null;
    }
    public SportPlanInfoResponse getSportPlan4Expt(GetInfo4ExptRequest getInfo) {
        return null;
    }
    public List<Categ4ExptVO> listTreatCateg4Expt( FindInterveneCateg4ExptRequest findTreat ){
        return null;
    }
    public ExptTreatPlanResponse listTreatItem4Expt( FindTreatList4ExptRequest findTreat ){
        return null;
    }

    //endregion

    //region 实验数据读写
    public ExptFoodCookbookResponse getExptFoodCookbook(ExptOrgFuncRequest exptOrgFunc ) {
        return null;
    }
    public SaveExptInterveneResponse saveExptFoodCookbook(SaveExptFoodRequest saveFood ) {
        return null;
    }
    public ExptFoodGraphResponse calcExptFoodGraph( SaveExptFoodRequest saveFood ){
        return null;
    }

    public ExptSportPlanResponse getExptSportPlan(ExptOrgFuncRequest exptOrgFunc ){
        return null;
    }
    public SaveExptInterveneResponse saveExptSportPlan(SaveExptSportRequest saveSport ) {
        return null;
    }
    public ExptTreatPlanResponse getExptTreatPlan(ExptOrgFuncRequest exptOrgFunc){
        return null;
    }
    public SaveExptTreatResponse saveExptTreatPlan( SaveExptTreatRequest saveTreat){
        return null;
    }

    //endregion



}