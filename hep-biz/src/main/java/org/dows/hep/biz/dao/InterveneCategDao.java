package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.hep.service.InterveneCategoryService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:37
 */
@Component
@RequiredArgsConstructor
public class InterveneCategDao {
    private final InterveneCategoryService interveneCategoryService;

    private final IdGenerator idGenerator;

    private final static SFunction<InterveneCategoryEntity,?> FUNCLogicKey=InterveneCategoryEntity::getInterveneCategoryId;

    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(InterveneCategoryEntity entity){
        if(ShareUtil.XObject.isEmpty(entity)) {
            return false;
        }
        if(ShareUtil.XObject.isEmpty(entity.getInterveneCategoryId())){
            entity.setInterveneCategoryId(idGenerator.nextIdStr());
        }
        return interveneCategoryService.saveOrUpdate(entity,Wrappers.<InterveneCategoryEntity>lambdaUpdate()
                .eq(FUNCLogicKey,entity.getInterveneCategoryId()));
    }
    @Transactional(rollbackFor = Exception.class)
    public boolean saveOrUpdate(List<InterveneCategoryEntity> entities){
        if(ShareUtil.XObject.isEmpty(entities)) {
            return false;
        }
        boolean rst=true;
        for(InterveneCategoryEntity item:entities){
            if(ShareUtil.XObject.isEmpty(item.getInterveneCategoryId())){
                item.setInterveneCategoryId(idGenerator.nextIdStr());
            }
            rst&=interveneCategoryService.saveOrUpdate(item,Wrappers.<InterveneCategoryEntity>lambdaUpdate()
                    .eq(FUNCLogicKey,item.getInterveneCategoryId()));
        }
        return rst;
    }

    public Optional<InterveneCategoryEntity> getById(String categId, SFunction<InterveneCategoryEntity, ?>... cols){
        if(ShareUtil.XObject.isEmpty(categId)){
            return Optional.empty();
        }
        return interveneCategoryService.lambdaQuery()
                .eq(FUNCLogicKey,categId)
                .select(cols)
                .oneOpt();
    }

    public List<InterveneCategoryEntity> getAll(){
        return interveneCategoryService.lambdaQuery()
                .orderByAsc(InterveneCategoryEntity::getFamily,InterveneCategoryEntity::getCategPid,
                        InterveneCategoryEntity::getSeq,InterveneCategoryEntity::getId)
                .list();
    }

    public List<InterveneCategoryEntity> listBySections(String sections, String pid, SFunction<InterveneCategoryEntity, ?>... cols){
        if(ShareUtil.XObject.isAllEmpty(sections,pid)){
            return Collections.emptyList();
        }
        String[] arr=sections.split(",|，");
        final boolean oneFlag=arr.length==1;

        return interveneCategoryService.lambdaQuery()
                .eq(oneFlag,InterveneCategoryEntity::getFamily,arr[0])
                .in(!oneFlag,InterveneCategoryEntity::getFamily,arr)
                .eq(!ShareUtil.XObject.isEmpty(pid),InterveneCategoryEntity::getCategPid,pid)
                .select(cols)
                .orderByAsc(InterveneCategoryEntity::getSeq,InterveneCategoryEntity::getId)
                .list();
    }

    public boolean delByIds(List<String> ids){
        if(ShareUtil.XCollection.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return interveneCategoryService.remove(Wrappers.<InterveneCategoryEntity>lambdaQuery()
                .eq(oneFlag,FUNCLogicKey,ids.get(0))
                .in(!oneFlag,FUNCLogicKey,ids));
    }








}
