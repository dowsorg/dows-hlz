package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import org.dows.framework.crud.api.CrudEntity;
import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 主从表操作基类
 *
 * @author : wuzl
 * @date : 2023/4/28 15:08
 */


public abstract class BaseSubDao<LS extends MybatisCrudService<LE>, LE extends CrudEntity,SS extends MybatisCrudService<SE>,SE extends CrudEntity>
    extends BaseCategDao<LS, LE> {

    protected BaseSubDao(String notExistsMessage){
        super(notExistsMessage);
    }

    @Autowired
    protected SS subService;


    //region virtual

    /**
     * get 主表id from 从表
     * @return
     */
    protected abstract SFunction<SE,String> getColLeadId();

    /**
     * set 主表id to 从表
     * @param item
     * @return
     */
    protected abstract SFunction<String,?> setColLeadId(SE item);

    /**
     * get 从表id from 从表
     * @return
     */
    protected abstract SFunction<SE,String> getColSubId();

    /**
     * set 从表id to 从表
     * @param item
     * @return
     */
    protected abstract SFunction<String,?> setColSubId(SE item);

    /**
     * get 序号 from 从表
     * @return
     */
    protected SFunction<SE,Integer> getColSubSeq(){
        return null;
    }

    /**
     * set 序号 to 从表
     * @param item
     * @return
     */
    protected SFunction<Integer,?> setColSubSeq(SE item){
        return null;
    }



    //从表查询 后置
    protected void postGetSubByLeadId(LambdaQueryChainWrapper<SE> wrapper) {
        if(null==getColSubSeq()){
            wrapper.orderByAsc(SE::getId);
            return;
        }
        wrapper.orderByAsc(getColSubSeq(),SE::getId);
    }

    //endregion




    //region tran

    /**
     * 批量主从保存事务
     * @param leads 主表记录列表
     * @param subs 从表记录列表
     * @param dftIfLeadEmpty 主表为空时返回
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveBatch(List<LE> leads, List<SE> subs,boolean dftIfLeadEmpty){
        AssertUtil.falseThenThrow(coreTranSaveBatch(leads, subs,defaultUseLogicId,dftIfLeadEmpty))
                .throwMessage(failedSaveMessage);
        return true;
    }
    /**
     *  主从保存事务
     * @param lead 主表记录
     * @param subs 从表记录列表
     * @param delSubBefore 是否先删除从表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(LE lead, List<SE> subs,boolean delSubBefore) {
        AssertUtil.falseThenThrow(coreTranSave(lead, subs,delSubBefore,defaultUseLogicId))
                .throwMessage(failedSaveMessage);
        return true;
    }


    /**
     * 主从删除事务
     * @param ids 主表逻辑主键列表
     * @param delSub 是否级联删除从表
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranDelete(List<String> ids,boolean delSub) {
        AssertUtil.falseThenThrow(coreTranDelete(ids,delSub,true))
                .throwMessage(notExistsMessage);
        return true;
    }

    /**
     * 按从表逻辑主键删除事务
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranDeleteSub(List<String> ids,String notExistsMessage){
        AssertUtil.falseThenThrow(delSubBySubId (ids,false))
                .throwMessage(notExistsMessage);
        return true;
    }

    //endregion

    //region save

    /**
     * 批量主从保存事务
     * @param leads
     * @param subs
     * @param useLogicId
     * @param dftIfLeadEmpty
     * @return
     */
    protected boolean coreTranSaveBatch(List<LE> leads, List<SE> subs,boolean useLogicId,boolean dftIfLeadEmpty){
        useLogicId=true;
        if(!saveOrUpdateBatch(leads,useLogicId,dftIfLeadEmpty)){
            return false;
        }
        if(ShareUtil.XObject.isEmpty(subs)) {
            return true;
        }
        subs.forEach(i->{
            if(ShareUtil.XObject.isEmpty(getColSubId().apply(i))) {
                setColSubId(i).apply(idGenerator.nextIdStr());
            }});

        if(!useLogicId){
            return subService.saveOrUpdateBatch(subs);
        }
        boolean rst=true;
        for(SE item:subs) {
            rst &= subService.saveOrUpdate(item, Wrappers.<SE>lambdaUpdate()
                    .eq(getColSubId(), getColSubId().apply(item)));
        }
        return rst;
    }

    /**
     *  主从保存事务
     * @param lead 主表记录
     * @param subs 从表记录
     * @param delSubBefore 是否先删除从表
     * @param useLogicId 是否使用逻辑主键
     * @return
     */
    protected boolean coreTranSave(LE lead, List<SE> subs,boolean delSubBefore,boolean useLogicId){
        final boolean existsFlag= ShareUtil.XObject.notEmpty(getColId().apply(lead));
        if(!saveOrUpdate(lead,useLogicId)) {
            return false;
        }
        final String leadId=getColId().apply(lead);
        if(existsFlag && delSubBefore) {
            delSubByLeadId(leadId,true);
            if(null!=subs) {
                subs.forEach(i -> i.setId(null));
            }
        }
        return saveOrUpdateBatch(leadId, subs,useLogicId,true);
    }


    /**
     * 批量保存从表
     * @param leadId 主表id
     * @param subs 从表记录
     * @param useLogicId 是否使用逻辑主键
     * @param dftIfEmpty 从表为空时返回值
     * @return
     */
    public boolean saveOrUpdateBatch(String leadId, Collection<SE> subs, boolean useLogicId, boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(subs)) {
            return dftIfEmpty;
        }
        int seq=0;
        for(SE item:subs){
            if(ShareUtil.XObject.notEmpty(leadId)) {
                setColLeadId(item).apply(leadId);
            }
            if(null!=setColSubSeq(item)) {
                setColSubSeq(item).apply(++seq);
            }
            if(ShareUtil.XObject.isEmpty(getColSubId().apply(item))){
                setColSubId(item).apply(idGenerator.nextIdStr());
            }
        }
        if(!useLogicId){
            return subService.saveOrUpdateBatch(subs);
        }
        boolean rst=true;
        for(SE item:subs) {
            rst &= subService.saveOrUpdate(item, Wrappers.<SE>lambdaUpdate()
                    .eq(getColSubId(), getColSubId().apply(item)));
        }
        return rst;
    }

    //endregion

    //region retrieve

    /**
     * 按从表物理主键获取
     * @param subPk
     * @param cols
     * @return
     */
    public Optional<SE> getSubBySubPk(String subPk, SFunction<SE,?>... cols) {
        if (ShareUtil.XObject.isEmpty(subPk)) {
            return Optional.empty();
        }
        return subService.lambdaQuery()
                .eq(SE::getId, subPk)
                .select(cols)
                .oneOpt();
    }

    /**
     * 按从表逻辑主键获取
     * @param subId
     * @param cols
     * @return
     */
    public Optional<SE> getSubBySubId(String subId, SFunction<SE,?>... cols){
        if(ShareUtil.XObject.isEmpty(subId)){
            return Optional.empty();
        }
        return subService.lambdaQuery()
                .eq(getColSubId(),subId)
                .select(cols)
                .oneOpt();
    }

    /**
     * 按主表id获取从表列表
     * @param leadId
     * @param cols
     * @return
     */
    public List<SE> getSubByLeadId(String leadId,SFunction<SE,?>... cols) {
        if (ShareUtil.XObject.isEmpty(leadId)) {
            return Collections.emptyList();
        }
        LambdaQueryChainWrapper wrapper = subService.lambdaQuery()
                .eq(getColLeadId(), leadId)
                .select(cols);
        postGetSubByLeadId(wrapper);
        return wrapper.list();
    }

    /**
     * 按多主表id获取从表列表
     * @param ids 主表id列表
     * @param cols 选择列
     * @return
     */
    public List<SE> getSubByLeadIds(Collection<String> ids,SFunction<SE,?>... cols){
        if(ShareUtil.XObject.isEmpty(ids)){
            return Collections.emptyList();
        }
        final boolean oneFlag=ids.size()==1;
        return subService.lambdaQuery()
                .eq(oneFlag, getColLeadId(),ids.iterator().next())
                .in(!oneFlag, getColLeadId(),ids)
                .orderByAsc(getColLeadId(),SE::getId)
                .select(cols)
                .list();
    }

    /**
     *  按多主表id获取map
     * @param ids 主表id列表
     * @param cols 选择列
     * @return
     */
    public Map<String,SE> getSubMapByLeadIds(Collection<String> ids, SFunction<SE, ?>... cols) {
        return ShareUtil.XCollection.toMap(getSubByLeadIds(ids, cols), getColLeadId(), Function.identity());
    }

    /**
     * 按多主表id获取map
     * @param ids
     * @param mapFactory
     * @param keyCol
     * @param preferNew
     * @param cols
     * @return
     * @param <K>
     */
    public <K> Map<K,SE> getSubMapByLeadIds(Collection<String> ids,Supplier<Map<K,SE>> mapFactory, SFunction<SE,K> keyCol,boolean preferNew, SFunction<SE, ?>... cols) {
        return ShareUtil.XCollection.toMap(getSubByLeadIds(ids, cols),mapFactory,  keyCol, Function.identity(),preferNew);
    }

    /**
     * 按多主表id获取分组
     * @param ids 主表id列表
     * @param groupByCol 分组列
     * @param selectCols 选择列，需包含分组列
     * @return
     * @param <K>
     */
    public <K> Map<K,List<SE>> getSubGroupByLeadIds(Collection<String> ids, SFunction<SE, K> groupByCol, SFunction<SE, ?>... selectCols){
        return ShareUtil.XCollection.groupBy(getSubByLeadIds(ids, selectCols),groupByCol);
    }

    /**
     * 按多主表id获取分组
     * @param ids 主表id列表
     * @param mapFactory map工厂
     * @param groupByCol 分组列
     * @param selectCols 选择列，需包含分组列
     * @return
     * @param <K>
     */
    public <K> Map<K,List<SE>> getSubGroupByLeadIds(Collection<String> ids, Supplier<Map<K,List<SE>>> mapFactory, SFunction<SE, K> groupByCol, SFunction<SE, ?>... selectCols){
        return ShareUtil.XCollection.groupBy(getSubByLeadIds(ids, selectCols),mapFactory, Function.identity(), groupByCol,Collectors.toList());
    }


    //endregion

    //region delete
    protected boolean coreTranDelete(List<String> ids,boolean delSub, boolean dftIfSubEmpty){
        if(!delByIds(ids)){
            return false;
        }
        if(delSub){
            delSubByLeadId(ids,dftIfSubEmpty);
        }
        return true;
    }

    /**
     * 按单个主表id删除从表
     * @param leadId 主表id
     * @param dftIfEmpty
     * @return
     */
    public boolean delSubByLeadId(String leadId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(leadId)){
            return dftIfEmpty;
        }
        return subService.remove(Wrappers.<SE>lambdaQuery()
                .eq(getColLeadId(),leadId));
    }

    /**
     * 按单个从表id删除从表
     * @param subId
     * @param dftIfEmpty
     * @return
     */
    public boolean delSubBySubId(String subId,boolean dftIfEmpty){
        if(ShareUtil.XObject.isEmpty(subId)){
            return dftIfEmpty;
        }
        return subService.remove(Wrappers.<SE>lambdaQuery()
                .eq(getColSubId(),subId));
    }
    /**
     * 按多个主表id删除从表
     * @param ids
     * @param dftIfEmpty
     * @return
     */
    public boolean delSubByLeadId(List<String> ids,boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subService.remove(Wrappers.<SE>lambdaQuery()
                .eq(oneFlag, getColLeadId(), ids.get(0))
                .in(!oneFlag, getColLeadId(), ids));

    }

    /**
     * 按多个从表id删除从表
     * @param ids
     * @param dftIfEmpty
     * @return
     */
    public boolean delSubBySubId(List<String> ids,boolean dftIfEmpty) {
        if (ShareUtil.XObject.isEmpty(ids)) {
            return dftIfEmpty;
        }
        final boolean oneFlag = ids.size() == 1;
        return subService.remove(Wrappers.<SE>lambdaQuery()
                .eq(oneFlag, getColSubId(), ids.get(0))
                .in(!oneFlag, getColSubId(), ids));

    }
    //endregion
}
