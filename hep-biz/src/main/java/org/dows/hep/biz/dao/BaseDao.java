package org.dows.hep.biz.dao;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.dows.framework.crud.api.CrudEntity;
import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

/**
 * @author : wuzl
 * @date : 2023/5/2 9:22
 */
public abstract class BaseDao<S extends MybatisCrudService<E>,E extends CrudEntity> {

    protected BaseDao(String notExistsMessage){
        this.notExistsMessage=ShareUtil.XString.defaultIfEmpty(notExistsMessage,this.notExistsMessage);
    }

    @Autowired
    protected S service;

    @Autowired
    protected IdGenerator idGenerator;


    protected String notExistsMessage="数据不存在或已删除，请刷新";
    protected String failedSaveMessage="保存失败";

    //region virtual

    /**
     * get 逻辑主键
     * @return
     */
    protected abstract SFunction<E,String> getColId();

    /**
     * set 逻辑主键
     * @param item
     * @return
     */
    protected abstract SFunction<String,?> setColId(E item);

    /**
     * get 状态字段
     * @return
     */
    protected abstract SFunction<E,Integer> getColState();

    /**
     * set 状态字段
     * @return
     */
    protected abstract SFunction<Integer,?> setColState(E item);



    //endregion


    //region tran

    /**
     * 保存事务
     * @param item
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSave(E item) {
        AssertUtil.falseThenThrow(saveOrUpdate(item))
                .throwMessage(failedSaveMessage);
        return true;
    }

    /**
     * 批量保存事务
     * @param items
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSaveBatch(Collection<E> items) {
        AssertUtil.falseThenThrow(saveOrUpdateBatch(items))
                .throwMessage(failedSaveMessage);
        return true;
    }

    /**
     * 设置状态事务
     * @param id
     * @param state
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranSetState(String id, Integer state) {
        AssertUtil.falseThenThrow(setState(id,state))
                .throwMessage(notExistsMessage);
        return true;
    }

    /**
     * 删除事务
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean tranDelete(List<String> ids) {
        AssertUtil.falseThenThrow(delByIds(ids))
                .throwMessage(notExistsMessage);
        return true;
    }
    //endregion

    //region save

    /**
     * 按物理主键单个保存
     * @param item
     * @return
     */
    public boolean saveOrUpdate(E item){
        return saveOrUpdate(item,false);
    }

    /**
     * 单个保存
     * @param item
     * @param useLogicId true-使用逻辑主键 false-使用物理主键
     * @return
     */
    public boolean saveOrUpdate(E item,boolean useLogicId){
        if(ShareUtil.XObject.isEmpty(item)){
            return false;
        }
        if(ShareUtil.XObject.isEmpty(getColId().apply(item))){
            setColId(item).apply(idGenerator.nextIdStr());
        }
        if(null!=setColState(item)){
            setColState(item).apply(EnumStatus.of(getColState().apply(item)).getCode());
        }
        if(!useLogicId){
            return service.saveOrUpdate(item);
        }
        return service.saveOrUpdate(item,Wrappers.<E>lambdaUpdate()
                .eq(getColId(),getColId().apply(item)));

    }

    /**
     * 按物理主键批量保存
     * @param items
     * @return
     */
    public boolean saveOrUpdateBatch(Collection<E> items){
        return saveOrUpdateBatch(items,false);
    }

    /**
     * 批量保存
     * @param items
     * @param useLogicId true-使用逻辑主键 false-使用物理主键
     * @return
     */

    public boolean saveOrUpdateBatch(Collection<E> items,boolean useLogicId){
        if(ShareUtil.XObject.isEmpty(items)){
            return false;
        }
        items.forEach(i->{
            if(ShareUtil.XObject.isEmpty(getColId().apply(i))){
                setColId(i).apply(idGenerator.nextIdStr());
            }
            if(null!=setColState(i)){
                setColState(i).apply(EnumStatus.of(getColState().apply(i)).getCode());
            }
        });
        if(!useLogicId){
            return service.saveOrUpdateBatch(items);
        }
        boolean rst=true;
        for(E item:items) {
            rst &= service.saveOrUpdate(item, Wrappers.<E>lambdaUpdate()
                    .eq(getColId(), getColId().apply(item)));
        }
        return rst;

    }

    /**
     * 设置启用禁用状态
     * @param id 逻辑主键
     * @param state 0-启用 1-禁用
     * @return
     */
    public boolean setState(String id,Integer state) {
        return service.update(Wrappers.<E>lambdaUpdate()
                .eq(getColId(), id)
                .set(getColState(), EnumStatus.of(state).getCode()));
    }
    //endregion

    //region retrieve

    /**
     * 按物理主键排序获取所有
     * @param isAsc
     * @param cols
     * @return
     */
    public List<E> getAll(boolean isAsc, SFunction<E,?>... cols){
        return service.lambdaQuery()
                .orderBy(true, isAsc, E::getId)
                .select(cols)
                .list();
    }
    /**
     * 按顺序获取所有
     * @param isAsc 是否正序
     * @param orderByCols 排序列
     * @param cols 选择列
     * @return
     */
    public List<E> getAll(boolean isAsc,List<SFunction<E,?>> orderByCols, SFunction<E,?>... cols){
        return service.lambdaQuery()
                .orderBy(ShareUtil.XCollection.notEmpty(orderByCols),isAsc,orderByCols)
                .select(cols)
                .list();
    }

    /**
     * 按物理主键查询
     * @param pk
     * @param cols
     * @return
     */
    public Optional<E> getByPk(Long pk, SFunction<E,?>... cols){
        if(ShareUtil.XObject.isEmpty(pk)){
            return Optional.empty();
        }
        return service.lambdaQuery()
                .eq(E::getId,pk)
                .select(cols)
                .oneOpt();
    }

    /**
     * 按逻辑主键查询
     * @param id
     * @param cols
     * @return
     */
    public Optional<E> getById(String id, SFunction<E,?>... cols){
        if(ShareUtil.XObject.isEmpty(id)){
            return Optional.empty();
        }
        return service.lambdaQuery()
                .eq(getColId(),id)
                .select(cols)
                .oneOpt();
    }

    /**
     * 按多逻辑主键获取列表
     * @param ids
     * @param cols
     * @return
     */

    public List<E> getByIds(Collection<String> ids,SFunction<E,?>... cols){
        if(ShareUtil.XObject.isEmpty(ids)){
            return Collections.emptyList();
        }
        final boolean oneFlag=ids.size()==1;
        return service.lambdaQuery()
                .eq(oneFlag, getColId(),ids.iterator().next())
                .in(!oneFlag, getColId(),ids)
                .select(cols)
                .list();
    }

    /**
     * 按多逻辑主键获取map
     * @param ids
     * @param cols
     * @return
     */
    public Map<String,E> getMapByIds(Collection<String> ids, SFunction<E, ?>... cols) {
        return ShareUtil.XCollection.toMap(getByIds(ids, cols), getColId(), Function.identity());
    }

    //endregion

    //region delete

    /**
     * 按逻辑主键单个或批量删除
     * @param ids
     * @return
     */
    public boolean delByIds(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return false;
        }
        final boolean oneFlag=ids.size()==1;
        return service.remove(Wrappers.<E>lambdaQuery()
                .eq(oneFlag, getColId(),ids.get(0))
                .in(!oneFlag, getColId(),ids));
    }

    //endregion
}
