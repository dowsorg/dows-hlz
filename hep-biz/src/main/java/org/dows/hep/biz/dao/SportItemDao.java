package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.service.SportItemIndicatorService;
import org.dows.hep.service.SportItemService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/25 17:27
 */
@Component
@RequiredArgsConstructor
public class SportItemDao {

    private final SportItemService sportItemService;

    private final SportItemIndicatorService sportItemIndicatorService;

    private final IdGenerator idGenerator;

    private final static SFunction<SportItemEntity,?> COLLeadId= SportItemEntity::getSportItemId;

    //region trans
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(SportItemEntity lead, List<SportItemIndicatorEntity> indicators) {
        AssertUtil.falseThenThrow(coreSave(lead, indicators))
                .throwMessage("保存失败,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranSetState(String materialId, Integer state) {
        AssertUtil.falseThenThrow(setState(materialId,state))
                .throwMessage("运动项目不存在,请刷新");
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean tranDelete(List<String> ids) {
        AssertUtil.falseThenThrow(coreDelete(ids))
                .throwMessage("运动项目不存在,请刷新");
        return true;
    }
    //endregion

    //region save
    public boolean coreSave(SportItemEntity lead,  List<SportItemIndicatorEntity> indicators){
        final boolean existsFlag=ShareUtil.XObject.notEmpty(lead.getSportItemId());
        if(!saveOrUpdate(lead)) {
            return false;
        }
        final String leadId=lead.getSportItemId();
        if(existsFlag) {
            delByLeadId4Indicator(leadId, true);
        }
        return saveBatch4Indicators(leadId, indicators);
    }
    public boolean saveOrUpdate(SportItemEntity lead){
        if(ShareUtil.XObject.isEmpty(lead)){
            return false;
        }
        if(ShareUtil.XObject.isAnyEmpty(lead.getSportItemId())){
            lead.setSportItemId(idGenerator.nextIdStr());
        }
        return sportItemService.saveOrUpdate(lead);
    }

    public boolean saveBatch4Indicators(String leadId,  List<SportItemIndicatorEntity> indicators){
        if(ShareUtil.XObject.isEmpty(indicators)){
            return true;
        }
        int seq=0;
        for(SportItemIndicatorEntity item:indicators){
            item.setId(null)
                    .setSportItemIndicatorId(idGenerator.nextIdStr())
                    .setSportItemId(leadId)
                    .setSeq(++seq);
        }
        return sportItemIndicatorService.saveBatch(indicators);
    }
    public boolean setState(String leadId,Integer state) {
        return sportItemService.update(Wrappers.<SportItemEntity>lambdaUpdate()
                .eq(COLLeadId, leadId)
                .set(SportItemEntity::getState, Optional.ofNullable(state).orElse(0)));
    }
    //endregion

    //region find
    public Page<SportItemEntity> getByCondition(Page<SportItemEntity> page, String keyWords, String categId, SFunction<SportItemEntity,?>... cols){

        return sportItemService.page(page,Wrappers.<SportItemEntity>lambdaQuery()
                .likeRight(ShareUtil.XString.hasLength(categId), SportItemEntity::getCategIdPath,categId)
                .like(ShareUtil.XString.hasLength(keyWords), SportItemEntity::getSportItemName,keyWords)
                .select(cols));

    }


    public Optional<SportItemEntity> getByPk(Long pk, SFunction<SportItemEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(pk)){
            return Optional.empty();
        }
        return sportItemService.lambdaQuery()
                .eq(SportItemEntity::getId,pk)
                .select(cols)
                .oneOpt();
    }

    public Optional<SportItemEntity> getById(String leadId, SFunction<SportItemEntity,?>... cols){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return Optional.empty();
        }
        return sportItemService.lambdaQuery()
                .eq(COLLeadId,leadId)
                .select(cols)
                .oneOpt();
    }

    public List<SportItemIndicatorEntity> getByLeadId(String leadId,SFunction<SportItemIndicatorEntity,?>... cols){
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        return sportItemIndicatorService.lambdaQuery()
                .eq(SportItemIndicatorEntity::getSportItemId, leadId)
                .orderByAsc(SportItemIndicatorEntity::getId)
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
        return sportItemService.remove(Wrappers.<SportItemEntity>lambdaQuery()
                .eq(oneFlag, COLLeadId,ids.get(0))
                .in(!oneFlag, COLLeadId,ids));
    }
    public boolean delByLeadId4Indicator(String leadId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return dftIfEmpty;
        }
        return sportItemIndicatorService.remove(Wrappers.<SportItemIndicatorEntity>lambdaQuery()
                .eq(SportItemIndicatorEntity::getSportItemId,leadId));
    }
    public boolean delByLeadId4Indicator(List<String> ids,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(ids)){
            return dftIfEmpty;
        }
        final boolean oneFlag=ids.size()==1;
        return sportItemIndicatorService.remove(Wrappers.<SportItemIndicatorEntity>lambdaQuery()
                .eq(oneFlag,SportItemIndicatorEntity::getSportItemId,ids.get(0))
                .in(!oneFlag,SportItemIndicatorEntity::getSportItemId,ids));

    }
    //endregion

}
