package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewBaseInfoRsResponse;
import org.dows.hep.api.enums.EnumIndicatorDocType;
import org.dows.hep.api.user.experiment.response.ExptHealthDocInfoResponse;
import org.dows.hep.api.user.experiment.vo.ExptIndicatorValLine;
import org.dows.hep.api.user.experiment.vo.ExptIndicatorValPoint;
import org.dows.hep.biz.dao.ExperimentIndicatorLogDao;
import org.dows.hep.biz.eval.EvalPersonCache;
import org.dows.hep.biz.eval.EvalPersonOnceHolder;
import org.dows.hep.biz.eval.data.EvalIndicatorValues;
import org.dows.hep.biz.eval.data.EvalPersonOnceData;
import org.dows.hep.biz.eval.data.EvalRiskValues;
import org.dows.hep.biz.event.ExperimentSettingCache;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.event.data.ExperimentSettingCollection;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.BigDecimalOptional;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;

/**
 * 健康档案接口
 * @author : wuzl
 * @date : 2023/9/6 9:38
 */
@Service
@RequiredArgsConstructor
public class ExperimentHealthDocBiz {

    private final EvalPersonCache evalPersonCache;

    private final ExperimentIndicatorLogDao experimentIndicatorLogDao;

    /**
     * 获取健康档案左上基本信息
     * @param appId
     * @param experimentPersonId
     * @return
     */
    public ExperimentIndicatorViewBaseInfoRsResponse getBaseInfo(String appId,String experimentInstanceId, String experimentPersonId){

        return null;
    }

    /**
     * 获取健康档案人物指标图
     * @param appId
     * @param experimentPersonId
     * @return
     */
    public ExptHealthDocInfoResponse getIndicatorInfo(String appId,String experimentInstanceId, String experimentPersonId){
        ExperimentCacheKey exptKey=ExperimentCacheKey.create(appId,experimentInstanceId);
        ExperimentSettingCollection exptColl= ExperimentSettingCache.Instance().getSet(exptKey,true);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(exptColl))
                .throwMessage("未找到实验设置");

        EvalPersonOnceHolder evalHolder= evalPersonCache.getCurHolder(experimentInstanceId, experimentPersonId);
        EvalPersonOnceData evalData= Optional.ofNullable(evalHolder)
                .map(EvalPersonOnceHolder::get)
                .orElse(null);
        EvalPersonOnceData.Header header= Optional.ofNullable(evalData)
                .map(EvalPersonOnceData::getHeader)
                .orElse(null);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(header))
                .throwMessage("未找到人物指标数据");

        List<ExperimentIndicatorLogEntity> rowsIndicator= experimentIndicatorLogDao.getDocIndicatorsByPersonId(experimentPersonId,
                ExperimentIndicatorLogEntity::getDocType,
                ExperimentIndicatorLogEntity::getExperimentIndicatorId,
                ExperimentIndicatorLogEntity::getExperimentIndicatorName,
                ExperimentIndicatorLogEntity::getUnit,
                ExperimentIndicatorLogEntity::getEvalDay,
                ExperimentIndicatorLogEntity::getCurVal);
        Map<String,ExperimentIndicatorLogEntity> mapDistinctByDay=ShareUtil.XCollection.toMap(rowsIndicator,
                LinkedHashMap::new,i->String.format("%s-%s", i.getExperimentIndicatorId(),i.getEvalDay()),  Function.identity(), true );
        rowsIndicator=mapDistinctByDay.values().stream().toList();
        mapDistinctByDay.clear();

        Map<EnumIndicatorDocType,Map< String, ExptIndicatorValLine>> mapTypeLines=new HashMap<>();
        rowsIndicator.forEach(i->{
            EnumIndicatorDocType docType=EnumIndicatorDocType.of(i.getDocType());
            if(docType==EnumIndicatorDocType.NONE){
                return;
            }
            Map< String, ExptIndicatorValLine> mapLines=mapTypeLines.computeIfAbsent(docType, k->new HashMap<>());
            ExptIndicatorValLine line= mapLines.computeIfAbsent(i.getExperimentIndicatorId(), k->new ExptIndicatorValLine()
                    .setPoints(new ArrayList<>())
                    .setIndicatorId(i.getExperimentIndicatorId())
                    .setIndicatorName(i.getExperimentIndicatorName())
                    .setUnit(i.getUnit())
            );
            line.getPoints().add(new ExptIndicatorValPoint()
                    .setGameDay(i.getEvalDay())
                    .setIndicatorValStr(i.getCurVal())
                    .setIndicatorVal(BigDecimalUtil.tryParseDecimalElseNull(i.getCurVal()))
            );
        });

        EvalIndicatorValues moneyValues=Optional.ofNullable( evalHolder.getMoneyValues()).orElse(new EvalIndicatorValues());
        ExptHealthDocInfoResponse rst=new ExptHealthDocInfoResponse();
        rst.setExperimentPersonId(experimentPersonId)
                .setHealthIndex(Optional.ofNullable( evalHolder.getHealthPoint(false))
                        .orElse(evalHolder.getHealthPoint(true)))
                .setMoney(evalHolder.getMoney(false))
                .setLastMoney(evalHolder.getMoney(true))
                .setMoneyScore(getMoneyScore(moneyValues.getCurVal(),moneyValues.getPeriodInitVal()))
                .setRisks(ShareUtil.XCollection.toSet(evalData.getRisks(), EvalRiskValues::getRiskName))
                .setTotalDays(exptColl.getTotalDays());

        mapTypeLines.forEach((k,v)->{
            switch (k){
                case HP -> rst.setHealthIndexLine(v.values().iterator().next());
                case BASIC -> rst.getCategOther().addAll(v.values());
                case ENERGY -> rst.getCategEnergy().addAll(v.values());

            }
        });
        mapTypeLines.clear();


        return rst;
    }



    private String getMoneyScore(String money,String lastMoney){
        if(ShareUtil.XObject.notNumber(money)
        ||ShareUtil.XObject.notNumber(lastMoney)) {
            return "-";
        }
        BigDecimal lastMoneyVal=BigDecimalUtil.tryParseDecimalElseNull(lastMoney);
        if(lastMoneyVal.compareTo(BigDecimal.ZERO)<=0){
            return "100%";
        }

        return BigDecimalOptional.valueOf(lastMoneyVal).sub(BigDecimalUtil.tryParseDecimalElseNull(money))
                .min(BigDecimal.ZERO)
                .div(lastMoneyVal,2)
                .mul(BigDecimalUtil.ONEHundred)
                .min(BigDecimal.ZERO)
                .max(BigDecimalUtil.ONEHundred)
                .getString(2)
                .concat("%");
    }
}
