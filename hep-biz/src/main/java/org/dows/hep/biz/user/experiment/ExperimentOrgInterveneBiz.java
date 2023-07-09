package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.FindFoodRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.response.*;
import org.dows.hep.api.base.intervene.vo.FoodCookbookDetailVO;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.api.core.ExptOperateOrgFuncRequest;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.api.enums.EnumFoodDetailType;
import org.dows.hep.api.enums.EnumFoodMealTime;
import org.dows.hep.api.user.experiment.request.*;
import org.dows.hep.api.user.experiment.response.ExptSportPlanResponse;
import org.dows.hep.api.user.experiment.response.ExptTreatPlanResponse;
import org.dows.hep.api.user.experiment.response.SaveExptInterveneResponse;
import org.dows.hep.api.user.experiment.response.SaveExptTreatResponse;
import org.dows.hep.api.user.experiment.vo.ExptTreatPlanItemVO;
import org.dows.hep.biz.base.intervene.*;
import org.dows.hep.biz.dao.OperateOrgFuncDao;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.dao.TreatItemDao;
import org.dows.hep.biz.util.*;
import org.dows.hep.biz.vo.CalcExptFoodCookbookResult;
import org.dows.hep.biz.vo.Categ4ExptVO;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.biz.vo.LoginContextVO;
import org.dows.hep.entity.*;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
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

    private final FoodCalc4ExptBiz foodCalc4ExptBiz;

    private final OperateOrgFuncDao operateOrgFuncDao;


    //region 快照数据查询
    private final InterveneCategBiz interveneCategBiz;
    private final FoodPlanBiz foodPlanBiz;
    private final FoodMaterialBiz foodMaterialBiz;

    private final SportPlanBiz sportPlanBiz;

    private final SportItemBiz sportItemBiz;

    private final TreatItemBiz treatItemBiz;

    private final SportItemDao sportItemDao;
    private final TreatItemDao treatItemDao;

    public List<Categ4ExptVO> listInterveneCateg4Expt(FindInterveneCateg4ExptRequest findCateg ) throws JsonProcessingException {
        FindInterveneCategRequest castReq=CopyWrapper.create(FindInterveneCategRequest::new).endFrom(findCateg);
        List<CategVO> items=interveneCategBiz.listInterveneCateg(castReq);
        if(ShareUtil.XObject.isEmpty(items)){
            return Collections.emptyList();
        }
        return JacksonUtil.deepCopy(items, true,new TypeReference<>() {});

    }

    public Page<FoodCookBookResponse> pageFoodCookbook4Expt(FindInterveneList4ExptRequest findFood ){
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodPlanBiz.pageFoodCookbook(castReq);
    }
    public FoodCookBookInfoResponse getFoodCookbook4Expt(GetInfo4ExptRequest getInfo) {
        return foodPlanBiz.getFoodCookbook(getInfo.getAppId(),getInfo.getInstanceId());
    }
    public Page<FoodDishesResponse> pageFoodDishes4Expt(FindInterveneList4ExptRequest findFood ) {
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodPlanBiz.pageFoodDishes(castReq);
    }
    public Page<FoodMaterialResponse> pageFoodMaterial4Expt( FindInterveneList4ExptRequest findFood ) {
        FindFoodRequest castReq=CopyWrapper.create(FindFoodRequest::new).endFrom(findFood);
        return foodMaterialBiz.pageFoodMaterial(castReq);
    }
    public Page<SportPlanResponse> pageSportPlan4Expt(FindInterveneList4ExptRequest findSport ){
        FindSportRequest castReq=CopyWrapper.create(FindSportRequest::new).endFrom(findSport);
        return sportPlanBiz.pageSportPlan(castReq);
    }
    public SportPlanInfoResponse getSportPlan4Expt(GetInfo4ExptRequest getInfo) {
        return sportPlanBiz.getSportPlan(getInfo.getAppId(),getInfo.getInstanceId());
    }
    public Page<SportItemResponse> pageSportItem4Expt(FindInterveneList4ExptRequest findSport ){
        FindSportRequest castReq=CopyWrapper.create(FindSportRequest::new).endFrom(findSport);
        return sportItemBiz.pageSportItem(castReq);
    }

    public List<Categ4ExptVO> listSportCateg4Expt(FindInterveneCateg4ExptRequest findSport ) throws JsonProcessingException{
        final String family=EnumCategFamily.SPORTItem.getCode();
        findSport.setFamily(family);
        findSport.setWithChild(1);
        FindInterveneCategRequest castReq=CopyWrapper.create(FindInterveneCategRequest::new).endFrom(findSport);
        List<CategVO> items=interveneCategBiz.listInterveneCateg(castReq);
        if(ShareUtil.XObject.isEmpty(items)){
            return Collections.emptyList();
        }
        List<Categ4ExptVO> rst= JacksonUtil.deepCopy(items, true,new TypeReference<>() {});
        Map<String,Categ4ExptVO> mapLeaf=getLeafMap(rst);
        if(ShareUtil.XObject.isEmpty(mapLeaf)){
            return rst;
        }
        FindSportRequest findReq=FindSportRequest.builder()
                .appId(findSport.getAppId())
                .build();
        Map<String,List<Categ4ExptVO>> mapItems=ShareUtil.XCollection.groupBy(sportItemDao.listByCondition (findReq,
                SportItemEntity::getInterveneCategId,
                SportItemEntity::getSportItemId,
                SportItemEntity::getSportItemName),row->Categ4ExptVO.builder()
                .categId(row.getSportItemId())
                .categName(row.getSportItemName())
                .categPid(row.getInterveneCategId())
                .build(),  Categ4ExptVO::getCategPid);
        mapLeaf.entrySet().forEach(i->{
            i.getValue().setChilds(mapItems.get(i.getKey()));
        });
        return rst;
    }
    public List<Categ4ExptVO> listTreatCateg4Expt( FindInterveneCateg4ExptRequest findTreat ) throws JsonProcessingException {
        findTreat.setWithChild(1);
        FindInterveneCategRequest castReq=CopyWrapper.create(FindInterveneCategRequest::new).endFrom(findTreat);
        List<CategVO> items=interveneCategBiz.listInterveneCateg(castReq);
        if(ShareUtil.XObject.isEmpty(items)){
            return Collections.emptyList();
        }
        List<Categ4ExptVO> rst= JacksonUtil.deepCopy(items, true,new TypeReference<>() {});
        Map<String,Categ4ExptVO> mapLeaf=getLeafMap(rst);
        if(ShareUtil.XObject.isEmpty(mapLeaf)){
            return rst;
        }
        final String indicatorFuncId=findTreat.getFamily().substring(EnumCategFamily.TreatItem.getCode().length());
        Map<String,List<Categ4ExptVO>> mapTreats=ShareUtil.XCollection.groupBy(treatItemDao.getByIndicatorFuncId(findTreat.getAppId(),indicatorFuncId,
                mapLeaf.size()<=200?mapLeaf.keySet():null,
                TreatItemEntity::getInterveneCategId,
                TreatItemEntity::getTreatItemId,
                TreatItemEntity::getTreatItemName),row->Categ4ExptVO.builder()
                .categId(row.getTreatItemId())
                .categName(row.getTreatItemName())
                .categPid(row.getInterveneCategId())
                .build(),  Categ4ExptVO::getCategPid);
        mapLeaf.entrySet().forEach(i->{
            i.getValue().setChilds(mapTreats.get(i.getKey()));
        });
        return rst;

    }
    Map<String,Categ4ExptVO>  getLeafMap(List<Categ4ExptVO> categs){
        if(ShareUtil.XCollection.isEmpty(categs)){
            return Collections.emptyMap();
        }
        Map<String,Categ4ExptVO> rst=new HashMap<>();
        categs.forEach(i->fillLeafMap(rst,i));
        return rst;
    }
    void fillLeafMap(Map<String,Categ4ExptVO> dst,Categ4ExptVO categ){
        if(ShareUtil.XObject.isEmpty(categ)){
            return;
        }
        if(ShareUtil.XCollection.isEmpty(categ.getChilds())){
            dst.put(categ.getCategId(),categ);
            return;
        }
        for(Categ4ExptVO item:categ.getChilds()) {
            fillLeafMap(dst, item);
        }
    }
    public List<TreatItemResponse>  listTreatItem4Expt( FindTreatList4ExptRequest findTreat ){
        FindTreatRequest castReq=FindTreatRequest.builder()
                .incIds(findTreat.getIncIds())
                .appId(findTreat.getAppId())
                .pageSize(Optional.ofNullable(findTreat.getIncIds()).map(List::size).orElse(10))
                .pageNo(1)
                .build();
        return treatItemBiz.pageTreatItem(castReq).getRecords();
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
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .checkOrgFlow(false);

        //计算营养统计，膳食宝塔
        CalcExptFoodCookbookResult snapRst= foodCalc4ExptBiz.calcFoodGraph4ExptCookbook(validator.getAppId(), saveFood.getDetails());
        snapRst.setDetails(saveFood.getDetails());
        //保存操作记录
        final Date dateNow=new Date();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getAppId(), validator.getExperimentInstanceId(),dateNow))
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
        return foodCalc4ExptBiz.calcFoodGraph4Expt(calcFoodGraph);
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
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator)
                .checkOrgFlow(false);

        //保存操作记录
        final Date dateNow=new Date();
        OperateOrgFuncEntity rowOrgFunc= createRowOrgFunc(validator)
                .setIndicatorCategoryId(operateType.getIndicatorCateg().getCode())
                .setOperateType(operateType.getCode())
                .setOperateAccountId(voLogin.getAccountId())
                .setOperateAccountName(voLogin.getAccountName())
                .setOperateTime(dateNow)
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getAppId(), validator.getExperimentInstanceId(),dateNow))
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
        ExptOrgFlowValidator flowValidator=ExptOrgFlowValidator.create(validator);
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
                .setOperateGameDay(ShareBiz.calcGameDay(validator.getAppId(), validator.getExperimentInstanceId(),dateNow))
                .setOperateFlowId(flowValidator.getOperateFlowId())
                .setReportFlag(operateType.getReportFuncFlag()?1:0)
                .setReportLabel(defOrgFunc.getName())
                .setReportDescr(String.format("%s了一次",defOrgFunc.getName()));
        //保存快照
        OperateOrgFuncSnapEntity rowOrgFuncSnap=new OperateOrgFuncSnapEntity()
                .setAppId(validator.getAppId())
                .setSnapTime(dateNow);
        for(int i=saveTreat.getTreatItems().size()-1;i>=0;i--){
            ExptTreatPlanItemVO item=saveTreat.getTreatItems().get(i);
            if(ShareUtil.XObject.notEmpty(item.getId(), true)){
                continue;
            }
            item.setId(getTimestampId(dateNow, i)).setDealFlag(0);
        }
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
        ExptRequestValidator validator=ExptRequestValidator.create(exptOperate)
                .checkExperimentPerson()
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
        if(ShareUtil.XObject.notEmpty(req.getOperateOrgFuncId())){
            return operateOrgFuncDao.getById(req.getOperateOrgFuncId(), cols);
        }
        return operateOrgFuncDao.getCurrentOrgFuncRecord(req.getExperimentPersonId(), req.getExperimentOrgId(),
                req.getIndicatorFuncId(), req.getPeriods(), cols);
    }

    private Long getTimestampId(Date dt,int seq){
        final int mod=10000;
        return getTimestampPrefix(dt)*mod+seq%mod;
    }

    private Long getTimestampPrefix(Date dt){
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmss");
        return Long.valueOf(sdf.format(dt));
    }

    //endregion



}