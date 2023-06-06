package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.enums.EnumFoodDetailType;
import org.dows.hep.api.enums.EnumFoodMealTime;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.ExptSportPlanResponse;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;
import org.dows.hep.api.user.experiment.response.SaveExptInterveneResponse;
import org.dows.hep.api.user.experiment.response.SaveExptTreatResponse;
import org.dows.hep.biz.base.intervene.FoodCalcBiz;
import org.dows.hep.biz.dao.OperateOrgFuncDao;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.CalcExptFoodCookbookResult;
import org.dows.hep.biz.vo.Categ4ExptVO;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.OperateFlowEntity;
import org.dows.hep.entity.OperateOrgFuncEntity;
import org.dows.hep.entity.OperateOrgFuncSnapEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
* @description project descr:实验:机构操作-操作指标
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class ExperimentOrgInterveneBiz{

    private final FoodCalcBiz foodCalcBiz;

    private final OperateOrgFuncDao operateOrgFuncDao;


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
    public CalcExptFoodCookbookResult getExptFoodCookbook(ExptOperateOrgFuncRequest exptOperate ) {
        return getExptSnapData(exptOperate,false,CalcExptFoodCookbookResult.class,CalcExptFoodCookbookResult::new);
    }
    public SaveExptInterveneResponse saveExptFoodCookbook(SaveExptFoodRequest saveFood, HttpServletRequest request) {
        ExptRequestValidator validator=ExptRequestValidator.create(saveFood);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance();
        saveFood.setDetails(ShareUtil.XObject.defaultIfNull(saveFood.getDetails(), Collections.emptyList()));
        //按餐次校验重复食材或菜肴
        Map<EnumFoodMealTime, List<String>> mapDetails = new HashMap<>();
        EnumFoodMealTime mealTime;
        EnumFoodDetailType detailType;
        for (FoodCookbookDetailVO item : saveFood.getDetails()) {
            detailType=EnumFoodDetailType.of(item.getInstanceType());
            AssertUtil.trueThenThrow( EnumFoodDetailType.NONE==detailType)
                    .throwMessage(String.format("不存在的明细类型"));
            mealTime = EnumFoodMealTime.of(item.getMealTime());
            AssertUtil.trueThenThrow(mealTime == EnumFoodMealTime.NONE)
                    .throwMessage("不存在的餐次");
            //生成食材描述
            if(EnumFoodDetailType.MATERIAL==detailType){
                item.setMaterialsDesc(String.format("%s100g",item.getInstanceName()));
            }
            mapDetails.computeIfAbsent(mealTime, k -> new ArrayList<>()).add(item.getInstanceId());
        }
        mapDetails.forEach((k, v) -> {
            AssertUtil.trueThenThrow(v.stream().distinct().count() < v.size())
                    .throwMessage(String.format("%s存在重复的菜肴或食材", k.getName()));
        });
        mapDetails.clear();

        //校验操作类型
        final EnumExptOperateType operateType=EnumExptOperateType.INTERVENEFood;
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        //校验挂号
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(saveFood);
        final Optional<OperateFlowEntity> flowOption=flowValidator.checkOrgFlowRunning();

        //计算营养统计，膳食宝塔
        CalcExptFoodCookbookResult snapRst=foodCalcBiz.calcFoodGraph4ExptCookbook(saveFood.getDetails());
        snapRst.setDetails(saveFood.getDetails());
        //保存操作记录
        final Date dateNow=new Date();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getExperimentInstance()))
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(operateType.getReportFuncFlag()?1:0)
                .setReportLabel("饮食干预")
                .setReportDescr("制定了一个食谱");
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        try{
            rowOrgFuncSnap.setInputJson(JacksonUtil.toJson(snapRst,true));
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据编制失败：%s",ex.getMessage()),ex);
        }
        //TODO saveFlow
        Boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc,Arrays.asList(rowOrgFuncSnap),false);
        return new SaveExptInterveneResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());

    }
    public CalcExptFoodCookbookResult calcExptFoodGraph( CalcExptFoodGraphRequest calcFoodGraph ){
        return foodCalcBiz.calcFoodGraph4Expt(calcFoodGraph);
    }

    public ExptSportPlanResponse getExptSportPlan(ExptOperateOrgFuncRequest exptOperate ){
        return getExptSnapData(exptOperate,false,ExptSportPlanResponse.class,ExptSportPlanResponse::new);
    }
    public SaveExptInterveneResponse saveExptSportPlan(SaveExptSportRequest saveSport, HttpServletRequest request ) {
        ExptRequestValidator validator=ExptRequestValidator.create(saveSport);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance();

        saveSport.setSportItems(ShareUtil.XObject.defaultIfNull(saveSport.getSportItems(), Collections.emptyList()));
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveSport.getSportItems())
                        &&saveSport.getSportItems().stream()
                        .map(SportPlanItemVO::getSportItemId)
                        .collect(Collectors.toSet())
                        .size()<saveSport.getSportItems().size())
                .throwMessage("存在重复的运动项目，请检查");
        //校验操作类型
        final EnumExptOperateType operateType=EnumExptOperateType.INTERVENESport;
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        //校验挂号
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(saveSport);
        final Optional<OperateFlowEntity> flowOption=flowValidator.checkOrgFlowRunning();

        //保存操作记录
        final Date dateNow=new Date();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getExperimentInstance()))
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(operateType.getReportFuncFlag()?1:0)
                .setReportLabel("运动干预")
                .setReportDescr("制定了一个运动方案");
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        ExptSportPlanResponse snapRst=new ExptSportPlanResponse().setSportItems(saveSport.getSportItems());
        try{
            rowOrgFuncSnap.setInputJson(JacksonUtil.toJson(snapRst,true));
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据编制失败：%s",ex.getMessage()),ex);
        }
        //TODO saveFlow
        Boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc,Arrays.asList(rowOrgFuncSnap),false);
        return new SaveExptInterveneResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());
    }
    public ExptTreatPlanResponse getExptTreatPlan(ExptOperateOrgFuncRequest exptOperate){
        return getExptSnapData(exptOperate,false,ExptTreatPlanResponse.class,ExptTreatPlanResponse::new);
    }
    public SaveExptTreatResponse saveExptTreatPlan( SaveExptTreatRequest saveTreat, HttpServletRequest request){
        ExptRequestValidator validator=ExptRequestValidator.create(saveTreat);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance()
                .checkIndicatorFunc();

        saveTreat.setTreatItems(ShareUtil.XObject.defaultIfNull(saveTreat.getTreatItems(), Collections.emptyList()));
        //校验操作类型
        EnumExptOperateType operateType=EnumExptOperateType.ofCategId(validator.getIndicatorCategoryId());
        AssertUtil.trueThenThrow(operateType==EnumExptOperateType.NONE)
                .throwMessage("未知的操作类型");
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);
        //校验挂号
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(saveTreat);
        final Optional<OperateFlowEntity> flowOption=flowValidator.checkOrgFlowRunning();
        //保存操作记录
        final Date dateNow=new Date();
        IndicatorFuncEntity defOrgFunc=validator.getIndicatorFunc();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getExperimentInstance()))
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(operateType.getReportFuncFlag()?1:0)
                .setReportLabel(defOrgFunc.getName())
                .setReportDescr(String.format("%s了一次",defOrgFunc.getName()));
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        ExptTreatPlanResponse snapRst=new ExptTreatPlanResponse().setTreatItems(saveTreat.getTreatItems());
        try{
            rowOrgFuncSnap.setInputJson(JacksonUtil.toJson(snapRst,true));
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据编制失败：%s",ex.getMessage()),ex);
        }
        //TODO saveFlowAndReport
        Boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc,Arrays.asList(rowOrgFuncSnap),false);
        return new SaveExptTreatResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());
    }

    private <T> T getExptSnapData(ExptOperateOrgFuncRequest exptOperate,boolean checkIndicatorFunc, Class<T> clazz, Supplier<T> creator){
        T rst=creator.get();
        ExptRequestValidator validator=ExptRequestValidator.create(exptOperate);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstance();
        if(checkIndicatorFunc){
            validator.checkIndicatorFunc();
        }
        OperateOrgFuncEntity rowOrgFunc=getRowOrgFunc(exptOperate, OperateOrgFuncEntity::getOperateOrgFuncId)
                .orElse(null);
        if(null==rowOrgFunc){
            return rst;
        }
        List<OperateOrgFuncSnapEntity> rowOrgFuncSnaps=operateOrgFuncDao.getSubByLeadId(rowOrgFunc.getOperateOrgFuncId(),OperateOrgFuncSnapEntity::getResultJson);
        if(ShareUtil.XObject.anyEmpty(rowOrgFuncSnaps,()->rowOrgFuncSnaps.get(0).getResultJson())){
            return rst;
        }
        try{
            return JacksonUtil.fromJson(rowOrgFuncSnaps.get(0).getInputJson(), clazz);
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据解析失败：%s",ex.getMessage()),ex);
        }
        return rst;
    }


    private OperateOrgFuncEntity createRowOrgFunc(ExptRequestValidator req){
        return OperateOrgFuncEntity.builder()
                .appId(req.getAppId())
                .experimentInstanceId(req.getExperimentInstanceId())
                .experimentGroupId(req.getExperimentGroupId())
                .experimentOrgId(req.getExperimentOrgId())
                .experimentPersonId(req.getExperimentPersonId())
                .periods(req.getPeriods())
                .indicatorCategoryId(req.getIndicatorCategoryId())
                .indicatorFuncId(req.getIndicatorFuncId())
                .build();
    }
    private Optional<OperateOrgFuncEntity> getRowOrgFunc(ExptOperateOrgFuncRequest req, SFunction<OperateOrgFuncEntity,?>... cols){
        if(ShareUtil.XObject.notEmpty(req.getOperateOrgFuncId())){
            return operateOrgFuncDao.getById(req.getOperateOrgFuncId(), cols);
        }
        return operateOrgFuncDao.getCurrentOrgFuncRecord(req.getExperimentPersonId(), req.getExperimentOrgId(),
                req.getIndicatorFuncId(), req.getPeriods(), cols);
    }

    //endregion



}