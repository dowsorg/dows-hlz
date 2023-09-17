package org.dows.hep.biz.spel.loaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.SnapCaseIndicatorExpressionDao;
import org.dows.hep.biz.dao.SnapCaseIndicatorExpressionItemDao;
import org.dows.hep.biz.dao.SnapCaseIndicatorExpressionRefDao;
import org.dows.hep.biz.spel.SnapshotRefValidator;
import org.dows.hep.biz.spel.meta.SpelInput;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionItemEntity;
import org.dows.hep.entity.snapshot.SnapCaseIndicatorExpressionRefEntity;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/21 11:05
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class FromSnapshotLoader extends BaseSpelLoader {

    private final SnapCaseIndicatorExpressionRefDao snapCaseIndicatorExpressionRefDao;
    private final SnapCaseIndicatorExpressionDao snapCaseIndicatorExpressionDao;
    private final SnapCaseIndicatorExpressionItemDao snapCaseIndicatorExpressionItemDao;

    @Override
    public SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source) {
        SpelInput rst=new SpelInput(source).setReasonId(reasonId);
        SnapshotRefValidator refValidator=new SnapshotRefValidator(experimentId);
        final String refExperimentId4ExpressionRef=refValidator.checkExpressionRef().getExpressionRefId();
        if(ShareUtil.XObject.isEmpty(refExperimentId4ExpressionRef)){
            return rst;
        }
        List<SnapCaseIndicatorExpressionRefEntity> rowsExpressionRef=snapCaseIndicatorExpressionRefDao.getByReasonId(refExperimentId4ExpressionRef, reasonId,
                SnapCaseIndicatorExpressionRefEntity::getReasonId,
                SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        if(ShareUtil.XObject.isEmpty(rowsExpressionRef)){
            logError("withReasonId", "miss expressionRef. experimentId:%s refExperimentId:%s reasonId:%s source:%s",
                    experimentId,refExperimentId4ExpressionRef,reasonId,source);
            return rst;
        }
        if(rowsExpressionRef.size()==1){
            return fillInput(rst,refValidator,  experimentPersonId, rowsExpressionRef.get(0).getIndicatorExpressionId(), source);
        }

        final String refExperimentId4Expression=refValidator.checkExpression().getExpressionId();
        final String refExperimentId4Item=refValidator.checkExpressionItem().getExpressionItemId();
        if(ShareUtil.XObject.anyEmpty(refExperimentId4Expression,refExperimentId4Item)){
            return rst;
        }
        final List<String> expressionIds=ShareUtil.XCollection.map(rowsExpressionRef, SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        List<SnapCaseIndicatorExpressionEntity> rowsExpression= snapCaseIndicatorExpressionDao.getByExpressionId(refExperimentId4Expression,expressionIds,source,
                SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId,
                SnapCaseIndicatorExpressionEntity::getCasePrincipalId,
                SnapCaseIndicatorExpressionEntity::getType,
                SnapCaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId
        );
        if(ShareUtil.XObject.isEmpty(rowsExpression)){
            logError("withReasonId","miss expressions. experimentId:%s refExperimentId:%s reasonId:%s source:%s",
                    experimentId,refExperimentId4Expression,reasonId,source);
            return rst;
        }
        if(rowsExpression.size()>1){
            logError("withReasonId", "more expressions. expereimentId:%s refExperimentId:%s reasonId:%s source:%s expressionIds:%s",
                    experimentId,refExperimentId4Expression,reasonId,source,String.join(",", expressionIds));
        }
        final String expressionId=rowsExpression.get(0).getCaseIndicatorExpressionId();
        List<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem=snapCaseIndicatorExpressionItemDao.getByExpressionId(refExperimentId4Item,expressionId,
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getConditionExpression,
                SnapCaseIndicatorExpressionItemEntity::getConditionNameList,
                SnapCaseIndicatorExpressionItemEntity::getConditionValList,
                SnapCaseIndicatorExpressionItemEntity::getResultExpression,
                SnapCaseIndicatorExpressionItemEntity::getResultNameList,
                SnapCaseIndicatorExpressionItemEntity::getResultValList,
                SnapCaseIndicatorExpressionItemEntity::getSeq
        );
        return fillInput(rst,experimentPersonId,rowsExpression.get(0),rowsExpressionItem);
    }

    @Override
    public List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source) {
        List<SpelInput> rst=new ArrayList<>();
        if(ShareUtil.XObject.isEmpty(reasonIds)){
            return rst;
        }
        SnapshotRefValidator refValidator=new SnapshotRefValidator(experimentId);
        final String refExperimentId4ExpressionRef=refValidator.checkExpressionRef().getExpressionRefId();
        final String refExperimentId4Expression=refValidator.checkExpression().getExpressionId();
        final String refExperimentId4Item=refValidator.checkExpressionItem().getExpressionItemId();
        if(ShareUtil.XObject.anyEmpty(refExperimentId4ExpressionRef,refExperimentId4Expression,refExperimentId4Item)){
            return rst;
        }
        List<SnapCaseIndicatorExpressionRefEntity> rowsExpressionRef=snapCaseIndicatorExpressionRefDao.getByReasonId(refExperimentId4ExpressionRef, reasonIds,
                SnapCaseIndicatorExpressionRefEntity::getReasonId,
                SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        if(ShareUtil.XObject.isEmpty(rowsExpressionRef)){
            logError("withReasonId", "miss expressionRef. experimentId:%s refExperimentId:%s reasonIds:%s source:%s",
                    experimentId,refExperimentId4ExpressionRef,String.join(",", reasonIds),source);
            return rst;
        }
        final List<String> expressionIds=ShareUtil.XCollection.map(rowsExpressionRef, SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId);
        List<SnapCaseIndicatorExpressionEntity> rowsExpression= snapCaseIndicatorExpressionDao.getByExpressionId(refExperimentId4Expression,expressionIds,source,
                SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId,
                SnapCaseIndicatorExpressionEntity::getCasePrincipalId,
                SnapCaseIndicatorExpressionEntity::getType,
                SnapCaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId
        );
        if(ShareUtil.XObject.isEmpty(rowsExpression)){
            logError("withReasonId","miss expressions. experimentId:%s refExperimentId:%s reasonIds:%s source:%s expressionIds:%s",
                    experimentId,refExperimentId4Expression,String.join(",", reasonIds),source,String.join(",", expressionIds));
            return rst;
        }
        Map<String,String> mapMinXMaxIds=new HashMap<>();
        rowsExpression.forEach(i->{
            if(ShareUtil.XObject.notEmpty(i.getMinIndicatorExpressionItemId())){
                mapMinXMaxIds.put(i.getMinIndicatorExpressionItemId(), i.getCaseIndicatorExpressionId());
            }
            if(ShareUtil.XObject.notEmpty(i.getMaxIndicatorExpressionItemId())){
                mapMinXMaxIds.put(i.getMaxIndicatorExpressionItemId(), i.getCaseIndicatorExpressionId());
            }
        });

        final List<String> itemExpressionIds=ShareUtil.XCollection.map(rowsExpression, SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId);
        List<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem=snapCaseIndicatorExpressionItemDao.getByExpressionId(refExperimentId4Item,itemExpressionIds,
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId,
                SnapCaseIndicatorExpressionItemEntity::getConditionExpression,
                SnapCaseIndicatorExpressionItemEntity::getConditionNameList,
                SnapCaseIndicatorExpressionItemEntity::getConditionValList,
                SnapCaseIndicatorExpressionItemEntity::getResultExpression,
                SnapCaseIndicatorExpressionItemEntity::getResultNameList,
                SnapCaseIndicatorExpressionItemEntity::getResultValList,
                SnapCaseIndicatorExpressionItemEntity::getSeq
        );
        List<SnapCaseIndicatorExpressionItemEntity> rowsMinMaxExpressionItem=snapCaseIndicatorExpressionItemDao.getByExpressionItemId(refExperimentId4Item,mapMinXMaxIds.keySet(),
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId,
                SnapCaseIndicatorExpressionItemEntity::getConditionExpression,
                SnapCaseIndicatorExpressionItemEntity::getConditionNameList,
                SnapCaseIndicatorExpressionItemEntity::getConditionValList,
                SnapCaseIndicatorExpressionItemEntity::getResultExpression,
                SnapCaseIndicatorExpressionItemEntity::getResultNameList,
                SnapCaseIndicatorExpressionItemEntity::getResultValList,
                SnapCaseIndicatorExpressionItemEntity::getSeq
        );
        rowsMinMaxExpressionItem.forEach(i->{
            i.setMinOrMax(true)
                    .setCaseIndicatorExpressionItemId(mapMinXMaxIds.get(i.getCaseIndicatorExpressionItemId()));
        });
        rowsExpressionItem.addAll(rowsMinMaxExpressionItem);

        Map<String,SnapCaseIndicatorExpressionEntity> mapExpression=ShareUtil.XCollection.toMap(rowsExpression, SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId);
        Map<String,List<SnapCaseIndicatorExpressionItemEntity>> mapExpressionItem=ShareUtil.XCollection.groupBy(rowsExpressionItem, SnapCaseIndicatorExpressionItemEntity::getIndicatorExpressionId);
        Map<String,String> mapExpressionRef=ShareUtil.XCollection.toMap(rowsExpressionRef,
                SnapCaseIndicatorExpressionRefEntity::getIndicatorExpressionId, SnapCaseIndicatorExpressionRefEntity::getReasonId);
        mapExpression.forEach((k,v)->{
            SpelInput input=new SpelInput(source).setReasonId(mapExpressionRef.get(k));
            rst.add(fillInput(input,experimentPersonId,v,mapExpressionItem.get(k)));
        });
        return rst;
    }

    @Override
    public SpelInput withExpressionId(String experimentId,String experimentPersonId,  String expressionId, Integer source) {
        return fillInput(new SpelInput(), new SnapshotRefValidator(experimentId), experimentPersonId, expressionId, source);
    }

    @Override
    public List<SpelInput> withExpressionId(String experimentId, String experimentPersonId, Collection<String> expressionIds, Integer source) {
        List<SpelInput> rst=new ArrayList<>();
        if(ShareUtil.XObject.isEmpty(expressionIds)){
            return rst;
        }
        SnapshotRefValidator refValidator=new SnapshotRefValidator(experimentId);
        final String refExperimentId4Expression=refValidator.checkExpression().getExpressionId();
        final String refExperimentId4Item=refValidator.checkExpressionItem().getExpressionItemId();
        if(ShareUtil.XObject.anyEmpty(refExperimentId4Expression,refExperimentId4Item)){
            return rst;
        }
        for(String expressionId:expressionIds){
            SpelInput input=fillInput(new SpelInput(), refValidator, experimentPersonId, expressionId, source);
            if(null==input){
                continue;
            }
            rst.add(input);
        }
        return rst;
    }

    //region fillInput
    protected SpelInput fillInput(SpelInput rst,SnapshotRefValidator refValidator, String experimentPersonId,  String expressionId, Integer source){
        if(null==rst){
            rst=new SpelInput(source);
        }
        rst.setExpressionId(expressionId);
        final String refExperimentId4Expression=refValidator.checkExpression().getExpressionId();
        final String refExperimentId4Item=refValidator.checkExpressionItem().getExpressionItemId();
        if(ShareUtil.XObject.anyEmpty(refExperimentId4Expression,refExperimentId4Item)){
            return rst;
        }
        SnapCaseIndicatorExpressionEntity rowExpression= snapCaseIndicatorExpressionDao.getByExpressionId(refExperimentId4Expression,expressionId,source,
                SnapCaseIndicatorExpressionEntity::getCaseIndicatorExpressionId,
                SnapCaseIndicatorExpressionEntity::getCasePrincipalId,
                SnapCaseIndicatorExpressionEntity::getType,
                SnapCaseIndicatorExpressionEntity::getMaxIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionEntity::getMinIndicatorExpressionItemId
        );
        if(ShareUtil.XObject.isEmpty(rowExpression)){
            logError("withExpressionId","miss expression. experimentId:%s refExperimentId:%s expressionId:%s source:%s",
                    refValidator.getExperimentId(),refExperimentId4Expression,expressionId,source);
            return rst;
        }
        List<SnapCaseIndicatorExpressionItemEntity> rowsExpressionItem=snapCaseIndicatorExpressionItemDao.getByExpressionId(refExperimentId4Item,expressionId,
                SnapCaseIndicatorExpressionItemEntity::getCaseIndicatorExpressionItemId,
                SnapCaseIndicatorExpressionItemEntity::getConditionExpression,
                SnapCaseIndicatorExpressionItemEntity::getConditionNameList,
                SnapCaseIndicatorExpressionItemEntity::getConditionValList,
                SnapCaseIndicatorExpressionItemEntity::getResultExpression,
                SnapCaseIndicatorExpressionItemEntity::getResultNameList,
                SnapCaseIndicatorExpressionItemEntity::getResultValList,
                SnapCaseIndicatorExpressionItemEntity::getSeq
        );
        return fillInput(rst,experimentPersonId,rowExpression,rowsExpressionItem);
    }
    //endregion




}
