package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.dows.hep.service.TreatItemIndicatorService;
import org.dows.hep.service.TreatItemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/26 15:44
 */

@Component
@RequiredArgsConstructor
public class TreatItemDao {
    private final TreatItemService treatItemService;

    private final TreatItemIndicatorService treatItemIndicatorService;

    private final IdGenerator idGenerator;

    private final static SFunction<TreatItemEntity,?> COLLeadId= TreatItemEntity::getTreatItemId;

    //region trans
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(TreatItemEntity lead, List<TreatItemIndicatorEntity> indicators) {
        AssertUtil.falseThenThrow(coreSave(lead, indicators))
                .throwMessage("保存失败,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSetState(String materialId, Integer state) {
        AssertUtil.falseThenThrow(setState(materialId,state))
                .throwMessage("干预项目不存在,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDelete(List<String> ids) {
        AssertUtil.falseThenThrow(coreDelete(ids))
                .throwMessage("干预项目不存在,请刷新");
        return true;
    }
    //endregion

    //region save
    public boolean coreSave(TreatItemEntity lead,  List<TreatItemIndicatorEntity> indicators){
        final boolean existsFlag=ShareUtil.XObject.notEmpty(lead.getTreatItemId());
        if(!saveOrUpdate(lead)) {
            return false;
        }
        final String leadId=lead.getTreatItemId();
        if(existsFlag) {
            delByLeadId4Indicator(leadId, true);
        }
        return saveBatch4Indicators(leadId, indicators);
    }
    public boolean saveOrUpdate(TreatItemEntity lead){
        if(ShareUtil.XObject.isEmpty(lead)){
            return false;
        }
        if(ShareUtil.XObject.isEmpty(lead.getTreatItemId())){
            lead.setTreatItemId(idGenerator.nextIdStr());
        }
        return treatItemService.saveOrUpdate(lead);
    }

    public boolean saveBatch4Indicators(String leadId,  List<TreatItemIndicatorEntity> indicators){
        if(ShareUtil.XObject.isEmpty(indicators)){
            return true;
        }
        int seq=0;
        for(TreatItemIndicatorEntity item:indicators){
            item.setId(null)
                    .setTreatItemIndicatorId(idGenerator.nextIdStr())
                    .setTreatItemId(leadId)
                    .setSeq(++seq);
        }
        return treatItemIndicatorService.saveBatch(indicators);
    }
    public boolean setState(String leadId,Integer state) {
        return treatItemService.update(Wrappers.<TreatItemEntity>lambdaUpdate()
                .eq(COLLeadId, leadId)
                .set(TreatItemEntity::getState, Optional.ofNullable(state).orElse(0)));
    }
    //endregion

    //region find
    public Page<TreatItemEntity> getByCondition(Page<TreatItemEntity> page, String keyWords, String categId, String funcId, SFunction<TreatItemEntity,?>... cols) {
        return treatItemService.page(page, Wrappers.<TreatItemEntity>lambdaQuery()
                .eq(TreatItemEntity::getIndicatorFuncId, funcId)
                .likeRight(ShareUtil.XString.hasLength(categId), TreatItemEntity::getCategIdPath, categId)
                .like(ShareUtil.XString.hasLength(keyWords), TreatItemEntity::getTreatItemName, keyWords)
                .select(cols));

    }


    public Optional<TreatItemEntity> getByPk(Long pk, SFunction<TreatItemEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(pk)){
            return Optional.empty();
        }
        return treatItemService.lambdaQuery()
                .eq(TreatItemEntity::getId,pk)
                .select(cols)
                .oneOpt();
    }

    public Optional<TreatItemEntity> getById(String leadId, SFunction<TreatItemEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return Optional.empty();
        }
        return treatItemService.lambdaQuery()
                .eq(COLLeadId,leadId)
                .select(cols)
                .oneOpt();
    }

    public List<TreatItemIndicatorEntity> getByLeadId(String leadId,SFunction<TreatItemIndicatorEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        return treatItemIndicatorService.lambdaQuery()
                .eq(TreatItemIndicatorEntity::getTreatItemId, leadId)
                .orderByAsc(TreatItemIndicatorEntity::getId)
                .select(cols)
                .list();
    }

    //endregion

    //region delete
    public boolean coreDelete(List<String> ids){
        if(!delByIds(ids)){
            return false;
        }
        delByLeadId4Indicator(ids,true);
        return true;
    }
    public boolean delByIds(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return treatItemService.remove(Wrappers.<TreatItemEntity>lambdaQuery()
                .eq(oneFlag, COLLeadId,ids.get(0))
                .in(!oneFlag, COLLeadId,ids));
    }
    public boolean delByLeadId4Indicator(String leadId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return dftIfEmpty;
        }
        return treatItemIndicatorService.remove(Wrappers.<TreatItemIndicatorEntity>lambdaQuery()
                .eq(TreatItemIndicatorEntity::getTreatItemId,leadId));
    }
    public boolean delByLeadId4Indicator(List<String> ids,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(ids)){
            return dftIfEmpty;
        }
        final boolean oneFlag=ids.size()==1;
        return treatItemIndicatorService.remove(Wrappers.<TreatItemIndicatorEntity>lambdaQuery()
                .eq(oneFlag,TreatItemIndicatorEntity::getTreatItemId,ids.get(0))
                .in(!oneFlag,TreatItemIndicatorEntity::getTreatItemId,ids));

    }
    //endregion
}
