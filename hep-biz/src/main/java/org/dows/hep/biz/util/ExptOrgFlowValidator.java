package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.core.BaseExptRequest;
import org.dows.hep.api.enums.EnumOrgFeeType;
import org.dows.hep.biz.dao.CaseOrgFeeDao;
import org.dows.hep.biz.dao.OperateFlowDao;
import org.dows.hep.entity.CaseOrgFeeEntity;
import org.dows.hep.entity.OperateFlowEntity;

import java.util.List;
import java.util.Optional;

/**
 * 学生端机构挂号校验
 * @author : wuzl
 * @date : 2023/6/5 15:52
 */
public class ExptOrgFlowValidator {

    private ExptOrgFlowValidator(BaseExptRequest req){
        this.appId=req.getAppId();
        this.experimentOrgId=req.getExperimentOrgId();
        this.experimentPersonId=req.getExperimentPersonId();
        this.periods=req.getPeriods();
    }

    //region create
    public static ExptOrgFlowValidator create(BaseExptRequest req){
        return new ExptOrgFlowValidator(req);
    }
    //endregion

    //region fileds
    //应用ID
    private String appId;
    //实验人物ID
    private String experimentPersonId;
    //实验机构ID
    private String experimentOrgId;
    //期数
    private Integer periods;
    //挂号流程id
    private String operateFlowId;

    private List<CaseOrgFeeEntity> exptFeeList;

    private Optional<OperateFlowEntity> exptFlow;
    //endregion

    //region getId
    public String getAppId(){
        return this.appId;
    }

    public String getExperimentPersonId(){
        return this.experimentPersonId;
    }
    public String getExperimentOrgId(){
        return this.experimentOrgId;
    }
    public Integer getPeriods(){
        return this.periods;
    }

    public String getOperateFlowId() {
        return this.operateFlowId;
    }
    //endregion

    //region orgFee

    /**
     * 获取挂号费
     * @return
     */
    public Optional<Double> getOrgFee4Ghf(){
        return getOrgFee(EnumOrgFeeType.GHF)
                .map(CaseOrgFeeEntity::getFee)
                .filter(i->i>0);
    }

    /**
     * 获取机构费用设置
     * @param feeType
     * @return
     */
    public Optional<CaseOrgFeeEntity> getOrgFee(EnumOrgFeeType feeType){
        List<CaseOrgFeeEntity> fees=getOrgFeeList(false);
        return fees.stream()
                .filter(i->feeType.getCode().equalsIgnoreCase( i.getFeeCode() ))
                .findFirst();

    }

    public ExptOrgFlowValidator checkOrgFeeList(boolean assertNotEmpty){
        getOrgFeeList(assertNotEmpty);
        return this;
    }
    public ExptOrgFlowValidator checkOrgFeeList(boolean assertNotEmpty, SFunction<CaseOrgFeeEntity,?>... cols){
        getOrgFeeList(assertNotEmpty,cols);
        return this;
    }
    public List<CaseOrgFeeEntity> getOrgFeeList(boolean assertNotEmpty){
        return getOrgFeeList(assertNotEmpty, CaseOrgFeeEntity::getCaseOrgId,
                CaseOrgFeeEntity::getFeeCode,
                CaseOrgFeeEntity::getFeeName,
                CaseOrgFeeEntity::getFee,
                CaseOrgFeeEntity::getReimburseRatio);
    }
    public List<CaseOrgFeeEntity> getOrgFeeList(boolean assertNotEmpty, SFunction<CaseOrgFeeEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentOrgId))
                .throwMessage("未找到实验机构ID");
        if(null==exptFeeList){
            exptFeeList= CrudContextHolder.getBean(CaseOrgFeeDao.class)
                    .getFeeList(experimentOrgId, cols);
        }
        AssertUtil.trueThenThrow(assertNotEmpty&&ShareUtil.XObject.isEmpty(this.exptFeeList ))
                .throwMessage("未找到机构费用设置");
        return exptFeeList;
    }
    //endregion

    //region flow

    /**
     * 校验需要挂号且已挂号
     * @return
     */
    public Optional<OperateFlowEntity> checkOrgFlowRunning(){
        Optional<Double> regFeeOption=getOrgFee4Ghf();
        AssertUtil.trueThenThrow(regFeeOption.isPresent()&&!ifOrgFlowRunning())
                .throwMessage("请先挂号");
        return this.exptFlow;
    }

    /**
     * 是否有已挂号的流程
     * @return
     */
    public boolean ifOrgFlowRunning(){
        return Optional.ofNullable( getOrgFlow(false))
                .filter(i->null==i.getEndTime())
                .isPresent();
    }
    public ExptOrgFlowValidator checkOrgFlow(boolean assertNotEmpty){
        getOrgFlow(assertNotEmpty);
        return this;
    }
    public ExptOrgFlowValidator checkOrgFlow(boolean assertNotEmpty,SFunction<OperateFlowEntity,?>... cols){
        getOrgFlow(assertNotEmpty, cols);
        return this;
    }
    public OperateFlowEntity getOrgFlow(boolean assertNotEmpty){
        return getOrgFlow(assertNotEmpty,
                OperateFlowEntity::getOperateFlowId,
                OperateFlowEntity::getStartTime,
                OperateFlowEntity::getEndTime,
                OperateFlowEntity::getTotalSteps,
                OperateFlowEntity::getDoneSteps);
    }
    public OperateFlowEntity getOrgFlow(boolean assertNotEmpty,SFunction<OperateFlowEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentPersonId))
                .throwMessage("未找到实验人物ID");
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentPersonId))
                .throwMessage("未找到实验人物ID");
        if(null==exptFlow){
            exptFlow=CrudContextHolder.getBean(OperateFlowDao.class)
                    .getCurrrentFlow(experimentPersonId, experimentOrgId, periods, cols);
        }
        AssertUtil.trueThenThrow(assertNotEmpty&&exptFlow.isEmpty())
                .throwMessage("未找到挂号记录");
        exptFlow.filter(i->ShareUtil.XObject.notEmpty(i.getOperateFlowId()))
                .ifPresent( i->operateFlowId=i.getOperateFlowId());
        return exptFlow.orElse(null);
    }
    //endregion

}
