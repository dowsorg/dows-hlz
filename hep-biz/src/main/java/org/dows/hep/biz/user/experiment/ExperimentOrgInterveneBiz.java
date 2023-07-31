package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.indicator.request.RsChangeMoneyRequest;
import org.dows.hep.api.base.indicator.request.RsExperimentCalculateFuncRequest;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.*;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.*;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;
import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeVO;
import org.dows.hep.api.user.experiment.vo.ExptTreatPlanItemVO;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorInstanceRsBiz;
import org.dows.hep.biz.base.indicator.RsExperimentCalculateBiz;
import org.dows.hep.biz.base.intervene.*;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.biz.dao.OperateOrgFuncDao;
import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.operate.CostRequest;
import org.dows.hep.biz.operate.OperateCostBiz;
import org.dows.hep.biz.orgreport.OrgReportComposer;
import org.dows.hep.biz.spel.PersonIndicatorIdCache;
import org.dows.hep.biz.spel.SpelInvoker;
import org.dows.hep.biz.spel.meta.SpelEvalResult;
import org.dows.hep.biz.spel.meta.SpelEvalSumResult;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.*;
import org.dows.hep.entity.*;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
@Slf4j
public class ExperimentOrgInterveneBiz{

    private final FoodCalc4ExptBiz foodCalc4ExptBiz;

    private final OrgReportComposer orgReportComposer;

    private final OperateFlowDao operateFlowDao;

    private final OperateOrgFuncDao operateOrgFuncDao;

    private final RsExperimentCalculateBiz rsExperimentCalculateBiz;


    //region 快照数据查询
    private final InterveneCategBiz interveneCategBiz;
    private final FoodPlanBiz foodPlanBiz;
    private final FoodMaterialBiz foodMaterialBiz;

    private final SportPlanBiz sportPlanBiz;

    private final SportItemBiz sportItemBiz;

    private final TreatItemBiz treatItemBiz;

    private final ExperimentPersonService experimentPersonService;

    private final IdGenerator idGenerator;

    private final OperateCostBiz operateCostBiz;
    
    private final ExperimentIndicatorInstanceRsBiz experimentIndicatorInstanceRsBiz;


    public List<Categ4ExptVO> listInterveneCateg4Expt(FindInterveneCateg4ExptRequest findCateg ) throws JsonProcessingException {
        ExptRequestValidator.create(findCateg).checkExperimentInstanceId();
        FindInterveneCategRequest castReq=CopyWrapper.create(FindInterveneCategRequest::new).endFrom(findCateg);
        List<CategVO> items=interveneCategBiz.listInterveneCateg(castReq);
        if(ShareUtil.XObject.isEmpty(items)){
            return Collections.emptyList();
        }
        return JacksonUtil.deepCopy(items, true,new TypeReference<>() {});

    }

    public Page<FoodCookBookResponse> pageFoodCookbook4Expt(FindInterveneList4ExptRequest findFood ){
        ExptRequestValidator.create(findFood).checkExperimentInstanceId();
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodPlanBiz.pageFoodCookbook(castReq);
    }
    public FoodCookBookInfoResponse getFoodCookbook4Expt(GetInfo4ExptRequest getInfo) {
        ExptRequestValidator.create(getInfo).checkExperimentInstanceId();
        return foodPlanBiz.getFoodCookbook(getInfo.getAppId(),getInfo.getInstanceId());
    }
    public Page<FoodDishesResponse> pageFoodDishes4Expt(FindInterveneList4ExptRequest findFood ) {
        ExptRequestValidator.create(findFood).checkExperimentInstanceId();
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodPlanBiz.pageFoodDishes(castReq);
    }
    public Page<FoodMaterialResponse> pageFoodMaterial4Expt( FindInterveneList4ExptRequest findFood ) {
        ExptRequestValidator.create(findFood).checkExperimentInstanceId();
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodMaterialBiz.pageFoodMaterial(castReq);
    }
    public Page<SportPlanResponse> pageSportPlan4Expt(FindInterveneList4ExptRequest findSport ){
        ExptRequestValidator.create(findSport).checkExperimentInstanceId();
        FindSportRequest castReq=CopyWrapper.create(FindSportRequest::new).endFrom(findSport);
        return sportPlanBiz.pageSportPlan(castReq);
    }
    public SportPlanInfoResponse getSportPlan4Expt(GetInfo4ExptRequest getInfo) {
        ExptRequestValidator.create(getInfo).checkExperimentInstanceId();
        return sportPlanBiz.getSportPlan(getInfo.getAppId(),getInfo.getInstanceId());
    }
    public Page<SportItemResponse> pageSportItem4Expt(FindInterveneList4ExptRequest findSport ){
        ExptRequestValidator.create(findSport).checkExperimentInstanceId();
        FindSportRequest castReq=CopyWrapper.create(FindSportRequest::new).endFrom(findSport);
        return sportItemBiz.pageSportItem(castReq);
    }

    public List<Categ4ExptVO> listSportCateg4Expt(FindInterveneCateg4ExptRequest findSport ) throws JsonProcessingException{
        findSport.setFamily(EnumCategFamily.SPORTItem.getCode());
        findSport.setWithChild(1);
        FindInterveneCateg4ExptRequest castReq=CopyWrapper.create(FindInterveneCateg4ExptRequest::new).endFrom(findSport);
        return listInterveneCateg4Expt(castReq);

    }
    public List<Categ4ExptVO> listTreatCateg4Expt( FindInterveneCateg4ExptRequest findTreat ) throws JsonProcessingException {
        findTreat.setWithChild(1);
        FindInterveneCateg4ExptRequest castReq=CopyWrapper.create(FindInterveneCateg4ExptRequest::new).endFrom(findTreat);
        return listInterveneCateg4Expt(castReq);
    }

    public List<TreatItemResponse>  listTreatItem4Expt( FindTreatList4ExptRequest findTreat ){
        ExptRequestValidator.create(findTreat).checkExperimentInstanceId();
        FindTreatRequest castReq=FindTreatRequest.builder()
                .incIds(findTreat.getIncIds())
                .appId(findTreat.getAppId())
                .indicatorFuncId(findTreat.getIndicatorFuncId())
                .pageSize(Optional.ofNullable(findTreat.getIncIds()).map(List::size).orElse(10))
                .pageNo(1)
                .build();
        return treatItemBiz.pageTreatItem(castReq).getRecords();
    }

    //endregion

    //region 实验数据读写
    public CalcExptFoodCookbookResult calcExptFoodGraph( CalcExptFoodGraphRequest calcFoodGraph ){
        ExptRequestValidator.create(calcFoodGraph).checkExperimentInstanceId();
        return foodCalc4ExptBiz.calcFoodGraph4Expt(calcFoodGraph);
    }
    public CalcExptFoodCookbookResult getExptFoodCookbook(ExptOperateOrgFuncRequest exptOperate ) {
        return getReportSnapData(exptOperate,false,false, CalcExptFoodCookbookResult.class,CalcExptFoodCookbookResult::new);
    }

    public ExptSportPlanResponse getExptSportPlan(ExptOperateOrgFuncRequest exptOperate ) {
        return getReportSnapData(exptOperate, false, false, ExptSportPlanResponse.class, ExptSportPlanResponse::new);
    }
    public ExptTreatPlanResponse getExptTreatPlan(ExptOperateOrgFuncRequest exptOperate){
        return getReportSnapData(exptOperate,false,true, ExptTreatPlanResponse.class,ExptTreatPlanResponse::new);
    }
    public SaveExptInterveneResponse saveExptFoodCookbook(SaveExptFoodRequest saveFood, HttpServletRequest request) {
        ExptRequestValidator validator=ExptRequestValidator.create(saveFood)
                .checkExperimentPerson()
                .checkExperimentOrgId()
                .checkExperimentInstanceId();
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
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .checkOrgFlow(false);

        //计算营养统计，膳食宝塔
        CalcExptFoodCookbookResult snapRst= foodCalc4ExptBiz.calcFoodGraph4ExptCookbook(validator.getAppId(), saveFood.getDetails());
        snapRst.setDetails(saveFood.getDetails());
        //保存操作记录

        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay())
                .setPeriods(timePoint.getPeriod())
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
        final PersonIndicatorIdCache cacheIndicatorId=PersonIndicatorIdCache.Instance();
        List<SpelEvalSumResult> evalSumResults=new ArrayList<>();
        for(CalcFoodStatVO item:snapRst.getStatEnergy()) {
            String indicatorId = cacheIndicatorId.getIndicatorIdBySourceId(validator.getExperimentPersonId(), item.getInstanceId());
            if (ShareUtil.XObject.isEmpty(indicatorId)) {
                continue;
            }
            evalSumResults.add(SpelEvalSumResult.builder()
                    .experimentIndicatorId(indicatorId)
                    .val(item.getWeight())
                    .build());
        }

        Boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc,Arrays.asList(rowOrgFuncSnap),false,()->{
            AssertUtil.falseThenThrow(SpelInvoker.Instance().saveIndicator(null, evalSumResults, timePoint.getPeriod()))
                    .throwMessage("影响指标数据保存失败");
            return true;
        });
        return new SaveExptInterveneResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());

    }

    public SaveExptInterveneResponse saveExptSportPlan(SaveExptSportRequest saveSport, HttpServletRequest request ) {
        ExptRequestValidator validator=ExptRequestValidator.create(saveSport);
        validator.checkExperimentPerson()
                .checkExperimentOrgId()
                .checkExperimentInstanceId();

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
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .checkOrgFlow(false);

        //保存操作记录
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay())
                .setPeriods(timePoint.getPeriod())
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

        boolean succFlag= operateOrgFuncDao.tranSave(rowOrgFunc,Arrays.asList(rowOrgFuncSnap),false);
        return new SaveExptInterveneResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());
    }

    public SaveExptTreatResponse saveExptTreatPlan( SaveExptTreatRequest saveTreat, HttpServletRequest request){
        // 获取总的费用资金
        BigDecimal sum = new BigDecimal(0);
        log.info("getTreatItems==========" + saveTreat.getTreatItems());
        for(int i = 0;i < saveTreat.getTreatItems().size(); i++){
            ExptTreatPlanItemVO vo = saveTreat.getTreatItems().get(i);
            log.info("vo==========" + vo);
            if(vo.getItemId() == null) {
                log.info("id=======" + vo.getItemId());
                log.info("flag =======" + String.valueOf(vo.getItemId() == null));
                sum = sum.add(BigDecimalOptional.valueOf(vo.getFee()).mul(BigDecimalUtil.tryParseDecimalElseZero(vo.getWeight())).getValue());
                log.info("111111111");
            }
        }
        log.info("sum=======================" + sum);

        ExptRequestValidator validator=ExptRequestValidator.create(saveTreat);
        validator.checkExperimentPerson()
                .checkExperimentOrg()
                .checkExperimentInstanceId()
                .checkIndicatorFunc();

        saveTreat.setTreatItems(ShareUtil.XObject.defaultIfNull(saveTreat.getTreatItems(), Collections.emptyList()));

        //校验操作类型
        EnumExptOperateType enumOperateType=EnumExptOperateType.ofCategId(validator.getIndicatorCategoryId());
        AssertUtil.trueThenThrow(enumOperateType==EnumExptOperateType.NONE)
                .throwMessage("未知的操作类型");
        //校验登录
        LoginContextVO voLogin= ShareBiz.getLoginUser(request);

        //校验挂号
        final LocalDateTime ldtNow=LocalDateTime.now();
        final Date dateNow=ShareUtil.XDate.localDT2Date(ldtNow);
        ExperimentTimePoint timePoint=validator.getTimePoint(true, ldtNow, true);
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .requireOrgFlowRunning(timePoint.getPeriod());
        //保存操作记录
        IndicatorFuncEntity defOrgFunc=validator.getIndicatorFunc();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(enumOperateType.getIndicatorCateg().getCode())
                .setOperateType(enumOperateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(timePoint.getGameDay())
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(enumOperateType.getReportFuncFlag()?1:0)
                .setReportLabel(validator.getCachedExptOrg().get().getExperimentOrgName())
                .setReportDescr(defOrgFunc.getName());
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        final List<ExptTreatPlanItemVO> newItems=new ArrayList<>();
        for(int i=saveTreat.getTreatItems().size()-1;i>=0;i--){
            ExptTreatPlanItemVO item=saveTreat.getTreatItems().get(i);
            item.setRawItemId(item.getItemId());
            if(ShareUtil.XObject.notEmpty(item.getItemId())){
                continue;
            }
            item.setItemId(getTimestampId(dateNow,saveTreat.getTreatItems().size()-i)).setDealFlag(0);
            newItems.add(item);
        }
        Map<String, SpelEvalSumResult> mapSum=new HashMap<>();
        List<SpelEvalResult> evalResults=SpelInvoker.Instance().evalTreatEffect(validator.getExperimentInstanceId(), validator.getExperimentPersonId(),
                timePoint.getPeriod(), newItems,mapSum);
        ExptTreatPlanResponse snapRst=new ExptTreatPlanResponse().setTreatItems(saveTreat.getTreatItems());
        try{
            rowOrgFuncSnap.setInputJson(JacksonUtil.toJson(snapRst,true))
                    .setResultJson(JacksonUtil.toJson(evalResults,true));
        }catch (Exception ex){
            AssertUtil.justThrow(String.format("记录数据编制失败：%s",ex.getMessage()),ex);
        }
        //挂号报告
        boolean succFlag=false;
        ExptOrgFlowReportResponse report=null;
        if(enumOperateType.getEndFlag()){
            OperateFlowEntity flow= flowValidator.getExptFlow().get();
            OperateFlowEntity saveFlow=OperateFlowEntity.builder()
                    .id(flow.getId())
                    .operateFlowId(flow.getOperateFlowId())
                    .operateAccountId(voLogin.getAccountId())
                    .operateAccountName(voLogin.getAccountName())
                    .periods(timePoint.getPeriod())
                    .reportFlag(1)
                    .reportLabel(validator.getCachedExptOrg().get().getExperimentOrgName())
                    .reportDescr(defOrgFunc.getName())
                    .endTime(dateNow)
                    .operateTime(dateNow)
                    .operateGameDay(timePoint.getGameDay())
                    .build();

            ExptOrgReportNodeVO node=new ExptOrgReportNodeVO()
                    .setNodeData(new ExptOrgReportNodeDataVO().setTreatFourLevel(snapRst))
                    .setIndicatorFuncId(validator.getIndicatorFuncId())
                    .setIndicatorCategoryId(validator.getIndicatorCategoryId())
                    .setIndicatorFuncName(validator.getIndicatorFuncName());
            OperateFlowSnapEntity saveFlowSnap=OperateFlowSnapEntity.builder()
                    .appId(rowOrgFunc.getAppId())
                    .snapTime(dateNow)
                    .build();
            try{
                report= orgReportComposer.composeReport(validator,flowValidator.updateFlowOperate(timePoint),timePoint,node);
                saveFlowSnap.setRecordJson(JacksonUtil.toJson(report,true));
            }catch (Exception ex){
                AssertUtil.justThrow(String.format("机构报告数据编制失败：%s",ex.getMessage()),ex);
            }

            succFlag=operateOrgFuncDao.tranSave(rowOrgFunc,List.of(rowOrgFuncSnap),false,()->{
                saveFlow.setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId());
                AssertUtil.falseThenThrow(operateFlowDao.tranSave(saveFlow, List.of(saveFlowSnap), false))
                        .throwMessage("机构报告数据保存失败");
                AssertUtil.falseThenThrow(SpelInvoker.Instance().saveIndicator(evalResults, mapSum.values(), timePoint.getPeriod()))
                        .throwMessage("影响指标数据保存失败");
                return true;
            });
        }else{
            succFlag=operateOrgFuncDao.tranSave(rowOrgFunc,List.of(rowOrgFuncSnap),false,()->{
                AssertUtil.falseThenThrow(SpelInvoker.Instance().saveIndicator(evalResults, mapSum.values(), timePoint.getPeriod()))
                        .throwMessage("影响指标数据保存失败");
                return true;
            });
        }
        // 判断是什么干预类型
        String feeName = "";
        String feeCode = "";
        if(enumOperateType == EnumExptOperateType.INTERVENETreatTwoLevel){
            feeName = EnumOrgFeeType.XLGYF.getName();
            feeCode = EnumOrgFeeType.XLGYF.getCode();
        }
        if(enumOperateType == EnumExptOperateType.INTERVENETreatFourLevel){
            feeName = EnumOrgFeeType.YWZLF.getName();
            feeCode = EnumOrgFeeType.YWZLF.getCode();
        }


        experimentIndicatorInstanceRsBiz.changeMoney(RsChangeMoneyRequest
                .builder()
                .appId(saveTreat.getAppId())
                .experimentId(saveTreat.getExperimentInstanceId())
                .experimentPersonId(saveTreat.getExperimentPersonId())
                .periods(timePoint.getPeriod())
                .moneyChange(sum.negate())
                .build());

        // 获取小组信息
        ExperimentPersonEntity personEntity = experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentPersonId,saveTreat.getExperimentPersonId())
                .eq(ExperimentPersonEntity::getDeleted,false)
                .one();
        // 保存消费记录
        CostRequest costRequest = CostRequest.builder()
                .operateCostId(idGenerator.nextIdStr())
                .experimentInstanceId(saveTreat.getExperimentInstanceId())
                .experimentGroupId(personEntity.getExperimentGroupId())
                .operatorId(voLogin.getAccountId())
                .experimentOrgId(saveTreat.getExperimentOrgId())
                .operateFlowId(flowValidator.getOperateFlowId())
                .patientId(saveTreat.getExperimentPersonId())
                .feeName(feeName)
                .feeCode(feeCode)
                .cost(sum)
                .period(timePoint.getPeriod())
                .build();
        operateCostBiz.saveCost(costRequest);
        if(enumOperateType.getEndFlag()) {
            CompletableFuture.runAsync(() -> {
                try {
                    rsExperimentCalculateBiz.experimentReCalculateFunc(RsExperimentCalculateFuncRequest.builder()
                            .appId(validator.getAppId())
                            .experimentId(validator.getExperimentInstanceId())
                            .periods(timePoint.getPeriod())
                            .experimentPersonId(validator.getExperimentPersonId())
                            .build());
                } catch (Exception ex) {
                    log.error(String.format("saveExptTreatPlan.deal experimentId:%s personId:%s",
                            validator.getExperimentInstanceId(), validator.getExperimentPersonId()), ex);
                }
            });
        }

        return new SaveExptTreatResponse()
                .setSuccess(succFlag)
                .setOperateOrgFuncId(rowOrgFunc.getOperateOrgFuncId())
                .setReportInfo(report);
    }


    private <T> T getReportSnapData(ExptOperateOrgFuncRequest reqOperateFunc, boolean checkIndicatorFunc, boolean checkOrgFlow, Class<T> clazz, Supplier<T> creator){
        T rst=creator.get();
        ExptRequestValidator validator=ExptRequestValidator.create(reqOperateFunc)
                .checkExperimentPerson()
                .checkExperimentOrgId()
                .checkExperimentInstanceId();
        if(checkIndicatorFunc){
            validator.checkIndicatorFunc();
        }
        if(checkOrgFlow&&ShareUtil.XObject.isEmpty(reqOperateFunc.getOperateFlowId())) {
            ExperimentTimePoint timePoint = validator.getTimePoint(false, LocalDateTime.now(), false);
            if (ShareUtil.XObject.isEmpty(timePoint)) {
                return rst;
            }
            ExptOrgFlowValidator flowValidator = ExptOrgFlowValidator.create(validator);
            if (!flowValidator.ifOrgFlowRunning(false, timePoint.getPeriod())) {
                return rst;
            }
            reqOperateFunc.setOperateFlowId(flowValidator.getOperateFlowId());
        }
        OperateOrgFuncEntity rowOrgFunc=getRowOrgFunc(reqOperateFunc,
                OperateOrgFuncEntity::getOperateOrgFuncId,
                OperateOrgFuncEntity::getOperateFlowId,
                OperateOrgFuncEntity::getPeriods)
                .orElse(null);
        if(null==rowOrgFunc){
            return rst;
        }
        List<OperateOrgFuncSnapEntity> rowOrgFuncSnaps=operateOrgFuncDao.getSubByLeadId(rowOrgFunc.getOperateOrgFuncId(),OperateOrgFuncSnapEntity::getInputJson);
        if(ShareUtil.XObject.anyEmpty(rowOrgFuncSnaps,()->rowOrgFuncSnaps.get(0).getInputJson())){
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
        /*if(ShareUtil.XObject.notEmpty(req.getOperateOrgFuncId())){
            return operateOrgFuncDao.getById(req.getOperateOrgFuncId(), cols);
        }*/
        return operateOrgFuncDao.getCurrentOrgFuncRecord(req.getExperimentPersonId(), req.getExperimentOrgId(),
                req.getIndicatorFuncId(), req.getPeriods(),req.getOperateFlowId(), cols);
    }

    private String getTimestampId(Date dt,int seq){
        final int mod=10000;
        return String.valueOf(getTimestampPrefix(dt)*mod+seq%mod);
    }

    private Long getTimestampPrefix(Date dt){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        return Long.valueOf(sdf.format(dt));
    }

    //endregion



}