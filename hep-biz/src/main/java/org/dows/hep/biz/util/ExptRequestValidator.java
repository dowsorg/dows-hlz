package org.dows.hep.biz.util;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.Getter;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.core.BaseExptRequest;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.entity.*;
import org.dows.hep.service.*;

import java.util.Optional;

/**
 * 学生端入参公用校验
 * @author : wuzl
 * @date : 2023/5/31 16:59
 */
public class ExptRequestValidator {

    private ExptRequestValidator(BaseExptRequest req){
        this.appId=req.getAppId();
        this.experimentInstanceId=req.getExperimentInstanceId();
        this.experimentGroupId=req.getExperimentGroupId();
        this.experimentOrgId=req.getExperimentOrgId();
        this.experimentPersonId=req.getExperimentPersonId();
        this.periods=req.getPeriods();

    }
    private ExptRequestValidator(ExptOrgFuncRequest req){
        this((BaseExptRequest) req);
        this.indicatorFuncId=req.getIndicatorFuncId();
    }
    //region create
    public static ExptRequestValidator create(BaseExptRequest req){
        return new ExptRequestValidator(req);
    }
    public static ExptRequestValidator create(ExptOrgFuncRequest req){
        return new ExptRequestValidator(req);
    }
    //endregion

    //region fields
    //应用ID
    @Getter
    private String appId;
    //实验实例ID
    @Getter
    private String experimentInstanceId;
    //实验小组ID
    @Getter
    private String experimentGroupId;
    //实验人物ID
    @Getter
    private String experimentPersonId;
    //实验机构ID
    @Getter
    private String experimentOrgId;
    //期数
    @Getter
    private Integer periods;

    //指标功能点ID
    @Getter
    private String indicatorFuncId;

    //功能点类别ID
    @Getter
    private String indicatorCategoryId;

    //案例ID
    @Getter
    private String caseInstanceId;

    //案例机构ID
    @Getter
    private String caseOrgId;
    //案例人物ID
    @Getter
    private String casePersonId;

    @Getter
    private Optional<ExperimentInstanceEntity> cachedExptInstance;

    @Getter
    private Optional<ExperimentGroupEntity> cachedExptGroup;
    @Getter
    private Optional<ExperimentPersonEntity> cachedExptPerson;
    @Getter
    private Optional<ExperimentOrgEntity> cachedExptOrg;

    @Getter
    private Optional<IndicatorFuncEntity> cachedExptOrgFunc;
    //endregion



    //region checkMulti
    public ExptRequestValidator checkValued(){
        if(ShareUtil.XObject.notEmpty(experimentInstanceId)){
            checkExperimentInstance();
        }
        if(ShareUtil.XObject.notEmpty(experimentGroupId)){
            checkExperimentGroup();
        }
        if(ShareUtil.XObject.notEmpty(experimentOrgId)){
            checkExperimentOrg();
        }
        if(ShareUtil.XObject.notEmpty(experimentPersonId)){
            checkExperimentPerson();
        }
        if(ShareUtil.XObject.notEmpty(indicatorFuncId)){
            checkIndicatorFunc();
        }
        return this;
    }
    //endregion

    //region checkExperimentInstance
    public ExptRequestValidator checkExperimentInstance(){
        getExperimentInstance();
        return this;
    }
    public ExptRequestValidator checkExperimentInstance(SFunction<ExperimentInstanceEntity,?>... cols){
        getExperimentInstance(cols);
        return this;
    }

    public ExperimentInstanceEntity getExperimentInstance(){
        return getExperimentInstance(ExperimentInstanceEntity::getExperimentInstanceId,
                ExperimentInstanceEntity::getCaseInstanceId,
                ExperimentInstanceEntity::getCaseName,
                ExperimentInstanceEntity::getExperimentName,
                ExperimentInstanceEntity::getExperimentDescr,
                ExperimentInstanceEntity::getModel,
                ExperimentInstanceEntity::getStartTime,
                ExperimentInstanceEntity::getState);
    }
    public ExperimentInstanceEntity getExperimentInstance(SFunction<ExperimentInstanceEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentInstanceId))
                .throwMessage("未找到实验ID");
        if(null== cachedExptInstance) {
            cachedExptInstance = CrudContextHolder.getBean(ExperimentInstanceService.class)
                    .lambdaQuery()
                    .eq(ShareUtil.XObject.notEmpty(this.appId),ExperimentInstanceEntity::getAppId,appId)
                    .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                    .select(cols)
                    .oneOpt();
        }
        ExperimentInstanceEntity rst= AssertUtil.getNotNull(cachedExptInstance).orElseThrow("未找到实验实例");
        if(ShareUtil.XObject.isEmpty(caseInstanceId)&&ShareUtil.XObject.notEmpty(rst.getCaseInstanceId())){
            caseInstanceId=rst.getCaseInstanceId();
        }
        return rst;
    }
    //endregion

    //region checkExperimentGroup
    public ExptRequestValidator checkExperimentGroup(){
        getExperimentGroup();
        return this;
    }

    public ExptRequestValidator checkExperimentGroup(SFunction<ExperimentGroupEntity,?>... cols){
        getExperimentGroup(cols);
        return this;
    }

    public ExperimentGroupEntity getExperimentGroup(){
        return getExperimentGroup(ExperimentGroupEntity::getExperimentGroupId,
                ExperimentGroupEntity::getExperimentInstanceId,
                ExperimentGroupEntity::getGroupNo,
                ExperimentGroupEntity::getGroupName,
                ExperimentGroupEntity::getGroupAlias,
                ExperimentGroupEntity::getMemberCount,
                ExperimentGroupEntity::getMinMemberCount,
                ExperimentGroupEntity::getMaxMemberCount,
                ExperimentGroupEntity::getState,
                ExperimentGroupEntity::getGroupState);
    }
    public ExperimentGroupEntity getExperimentGroup(SFunction<ExperimentGroupEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentGroupId))
                .throwMessage("未找到实验小组ID");
        if(null== cachedExptGroup) {
            cachedExptGroup = CrudContextHolder.getBean(ExperimentGroupService.class)
                    .lambdaQuery()
                    .eq(ShareUtil.XObject.notEmpty(this.appId),ExperimentGroupEntity::getAppId,appId)
                    .eq(ExperimentGroupEntity::getExperimentGroupId, experimentGroupId)
                    .select(cols)
                    .oneOpt();
        }
        ExperimentGroupEntity rst=AssertUtil.getNotNull(cachedExptGroup).orElseThrow("未找到实验小组");
        if(ShareUtil.XObject.isEmpty(experimentInstanceId)&&ShareUtil.XObject.notEmpty(rst.getExperimentInstanceId())){
            experimentInstanceId=rst.getExperimentInstanceId();
        }
        return rst;
    }
    //endregion

    //region checkExperimentOrg
    public ExptRequestValidator checkExperimentOrg(){
        getExperimentOrg();
        return this;
    }
    public ExptRequestValidator checkExperimentOrg(SFunction<ExperimentOrgEntity,?>... cols){
        getExperimentOrg(cols);
        return this;
    }

    public ExperimentOrgEntity getExperimentOrg(){
        return getExperimentOrg(ExperimentOrgEntity::getExperimentOrgId,
                ExperimentOrgEntity::getExperimentOrgName,
                ExperimentOrgEntity::getExperimentInstanceId,
                ExperimentOrgEntity::getExperimentGroupId,
                ExperimentOrgEntity::getCaseOrgId,
                ExperimentOrgEntity::getCaseOrgName);
    }
    public ExperimentOrgEntity getExperimentOrg(SFunction<ExperimentOrgEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentPersonId))
                .throwMessage("未找到实验人物ID");
        if(null== cachedExptOrg) {
            cachedExptOrg = CrudContextHolder.getBean(ExperimentOrgService.class)
                    .lambdaQuery()
                    .eq(ShareUtil.XObject.notEmpty(this.appId),ExperimentOrgEntity::getAppId,appId)
                    .eq(ExperimentOrgEntity::getExperimentOrgId, experimentOrgId)
                    .select(cols)
                    .oneOpt();
        }
        ExperimentOrgEntity rst=AssertUtil.getNotNull(cachedExptOrg).orElseThrow("未找到实验机构");
        if(ShareUtil.XObject.isEmpty(experimentInstanceId)&&ShareUtil.XObject.notEmpty(rst.getExperimentInstanceId())){
            experimentInstanceId=rst.getExperimentInstanceId();
        }
        if(ShareUtil.XObject.isEmpty(experimentGroupId)&&ShareUtil.XObject.notEmpty(rst.getExperimentGroupId())){
            experimentGroupId=rst.getExperimentGroupId();
        }
        if(ShareUtil.XObject.isEmpty(caseOrgId)&&ShareUtil.XObject.notEmpty(rst.getCaseOrgId())){
            caseOrgId=rst.getCaseOrgId();
        }
        return rst;
    }
    //endregion

    //region checkExperimentPerson
    public ExptRequestValidator checkExperimentPerson(){
        getExperimentPerson();
        return this;
    }
    public ExptRequestValidator checkExperimentPerson(SFunction<ExperimentPersonEntity,?> cols){
        getExperimentPerson(cols);
        return this;
    }
    public ExperimentPersonEntity getExperimentPerson(){
        return getExperimentPerson(ExperimentPersonEntity::getExperimentPersonId,
                ExperimentPersonEntity::getAccountName,
                ExperimentPersonEntity::getCasePersonId,
                ExperimentPersonEntity::getExperimentInstanceId,
                ExperimentPersonEntity::getExperimentGroupId,
                ExperimentPersonEntity::getExperimentOrgId);
    }
    public ExperimentPersonEntity getExperimentPerson(SFunction<ExperimentPersonEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(experimentPersonId))
                .throwMessage("未找到实验人物ID");
        if(null== cachedExptPerson) {
            cachedExptPerson = CrudContextHolder.getBean(ExperimentPersonService.class)
                    .lambdaQuery()
                    .eq(ShareUtil.XObject.notEmpty(this.appId),ExperimentPersonEntity::getAppId,appId)
                    .eq(ExperimentPersonEntity::getExperimentPersonId, experimentPersonId)
                    .select(cols)
                    .oneOpt();
        }
        ExperimentPersonEntity rst=AssertUtil.getNotNull(cachedExptPerson).orElseThrow("未找到实验人物");
        if(ShareUtil.XObject.isEmpty(experimentInstanceId) &&ShareUtil.XObject.notEmpty(rst.getExperimentInstanceId())){
            experimentInstanceId=rst.getExperimentInstanceId();
        }
        if(ShareUtil.XObject.isEmpty(experimentGroupId) &&ShareUtil.XObject.notEmpty(rst.getExperimentGroupId())){
            experimentGroupId=rst.getExperimentGroupId();
        }
        if(ShareUtil.XObject.isEmpty(experimentOrgId) &&ShareUtil.XObject.notEmpty(rst.getExperimentOrgId())){
            experimentOrgId=rst.getExperimentOrgId();
        }
        if(ShareUtil.XObject.isEmpty(casePersonId)&&ShareUtil.XObject.notEmpty(rst.getCasePersonId())){
            casePersonId=rst.getCasePersonId();
        }
        return rst;
    }
    //endregion

    //region checkIndicatorFunc
    public ExptRequestValidator checkIndicatorFunc(){
        getIndicatorFunc();
        return this;
    }
    public ExptRequestValidator checkIndicatorFunc(SFunction<IndicatorFuncEntity,?>... cols){
        getIndicatorFunc(cols);
        return this;
    }

    public IndicatorFuncEntity getIndicatorFunc(){
        return getIndicatorFunc(IndicatorFuncEntity::getIndicatorFuncId,
                IndicatorFuncEntity::getPid,
                IndicatorFuncEntity::getIndicatorCategoryId,
                IndicatorFuncEntity::getName);
    }
    public IndicatorFuncEntity getIndicatorFunc(SFunction<IndicatorFuncEntity,?>... cols){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(indicatorFuncId))
                .throwMessage("未找到机构功能ID");
        if(null== cachedExptOrgFunc){
            cachedExptOrgFunc =CrudContextHolder.getBean(IndicatorFuncService.class)
                    .lambdaQuery()
                    .eq(ShareUtil.XObject.notEmpty(this.appId),IndicatorFuncEntity::getAppId,appId)
                    .eq(IndicatorFuncEntity::getIndicatorFuncId, indicatorFuncId)
                    .select(cols)
                    .oneOpt();
        }
        IndicatorFuncEntity rst=AssertUtil.getNotNull(cachedExptOrgFunc).orElseThrow("未找到机构功能");
        if(ShareUtil.XObject.isEmpty(indicatorCategoryId)&&ShareUtil.XObject.notEmpty(rst.getIndicatorCategoryId())){
            indicatorCategoryId=rst.getIndicatorCategoryId();
        }
        return rst;
    }
    //endregion








}
