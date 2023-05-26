package org.dows.hep.biz.cache;


import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wuzl
 * @date : 2023/4/23 11:30
 */

public abstract class CategCache extends BaseLocalCache<CategCache.CacheData> {

    //region .ctor
    protected final String SPLITCategPath ="/";
    protected CategCache(long expireInMinutes){
        super(expireInMinutes);
    }
    //endregion


    @Override
    protected CacheData load() {
        CacheData vCache=new CacheData();
        List<CategVO> src=loadFromDb();
        if(ShareUtil.XCollection.isEmpty(src)) {
            return vCache;
        }
        src.forEach(i->{
            i.setCategPid(Optional.ofNullable(i.getCategPid()).orElse(""));
            vCache.mapItems.put(fixKey(i.getCategId()),i);
            vCache.mapGroups.computeIfAbsent(fixKey(ShareUtil.XString.defaultIfEmpty(i.getCategPid(),i.getFamily())),v-> new ArrayList<>())
                    .add(i);
        });
        StringBuilder sbId=new StringBuilder();
        StringBuilder sbName=new StringBuilder();
        src.forEach(i-> {
            //设置父子关联
            i.setChilds(vCache.mapGroups.get(i.getCategId()));
            sbId.setLength(0);
            sbName.setLength(0);
            CategVO parent = getParent(vCache, sbId, sbName, i);
            i.setCategIdPath(sbId.toString())
                    .setCategNamePath(sbName.toString())
                    .setFamily(parent.getFamily());
        });
        src.clear();
        //TODO 分布式处理
        return vCache;
    }
    //填充父级路径
    protected CategVO getParent(CacheData cache, StringBuilder sbId, StringBuilder sbName, CategVO child) {

        if (ShareUtil.XObject.isEmpty(child)) {
            return null;
        }
        sbId.insert(0, String.format("%s%s", child.getCategId(), SPLITCategPath));
        sbName.insert(0, String.format("%s%s", child.getCategName(), SPLITCategPath));
        CategVO parent = cache.mapItems.get(child.getCategPid());
        if (null == parent) {
            return child;
        }
        return getParent(cache,sbId, sbName, parent);
    }


    //region facade

    /**
     * 按id获取单类别
     * @param categId
     * @return
     */
    public CategVO getById(String categId){
        if(ShareUtil.XObject.isEmpty(categId)){
            return null;
        }
        return ensureCache().mapItems.get(fixKey(categId));
    }

    /**
     * 获取根类别下所有子类别
     * @param family 根类别
     * @param withChild 是否包含子节点
     * @return
     */
    public List<CategVO> getByFamily(EnumCategFamily family, boolean withChild) {
        List<CategVO> rst= ensureCache().mapGroups.get(fixKey(family.getCode()));
        return withChild?rst:flatCopy(rst);
    }

    /**
     * 获取父id下所有子类别
     * @param categId 父类别
     * @param withChild 是否包含子节点
     * @return
     */
    public List<CategVO> getByParentId(String categId,boolean withChild) {
        if(ShareUtil.XObject.isEmpty(categId)){
            return Collections.emptyList();
        }
        List<CategVO> rst=  ensureCache().mapGroups.get(fixKey(categId));
        return withChild?rst:flatCopy(rst);
    }

    /**
     * 获取id下所有的叶节点id
     * @param categIds
     * @return
     */
    public List<String> getLeafIds(List<String> categIds){
        if(ShareUtil.XCollection.isEmpty(categIds)){
            return Collections.emptyList();
        }
        Set<String> rst=new HashSet<>();
        categIds.forEach(i->fillLeafIds(rst,getById(i)));
        return new ArrayList<>(rst);
    }
    protected void fillLeafIds(Collection<String> rst,CategVO categ){
        if(ShareUtil.XObject.isEmpty(categ)){
            return;
        }
        if(ShareUtil.XCollection.isEmpty(categ.getChilds())){
            rst.add(categ.getCategId());
            return;
        }
        for(CategVO item:categ.getChilds()) {
            fillLeafIds(rst, item);
        }
    }

    /**
     * 获取不带子节点的列表
     *
     * @param src
     * @return
     */
    public List<CategVO> flatCopy(List<CategVO> src){
        if(ShareUtil.XObject.isEmpty(src)){
            return Collections.emptyList();
        }
        return ShareUtil.XCollection.map(src, i-> CopyWrapper.create(CategVO::new).endFrom(i,"childs"));
    }


    /**
     * 获取一级目录
     * @param path
     * @param self
     * @return
     */
    public String getCategLv1(String path,String self){
        if(ShareUtil.XObject.isEmpty(path)){
            return self;
        }
        return path.split(SPLITCategPath)[0];
    }

    /**
     * 获取子级目录路径
     *
     * @param path
     * @param self
     * @return
     */
    public String getCategPath(String path,String self){
        if(ShareUtil.XObject.isEmpty(path)){
            return ShareUtil.XString.eusureEndsWith(self,SPLITCategPath);
        }
        return String.format("%s%s", ShareUtil.XString.eusureEndsWith(path,SPLITCategPath),
                ShareUtil.XString.eusureEndsWith(self,SPLITCategPath));
    }

    public String getSplitCategPath(){
        return SPLITCategPath;
    }


    @Override
    public boolean isExpired() {
        //TODO 分布式处理
        return super.isExpired();
    }

    @Override
    public void clear() {
        super.clear();
        //TODO 分布式处理
    }

    //endregion

    protected abstract List<CategVO> loadFromDb();
    @Override
    protected void clearOld(CacheData old) {
        if(null!=old){
            old.clear();
        }
    }

    protected String fixKey(String key){
        return ShareUtil.XString.defaultIfEmpty(key,"").toLowerCase();
    }

    public static class CacheData {
        protected final Map<String, List<CategVO>> mapGroups=new ConcurrentHashMap<>();
        protected final Map<String, CategVO> mapItems=new ConcurrentHashMap<>();


        protected void clear(){
            for (CategVO item:  mapItems.values()){
                item.setChilds(null);
            }
            mapGroups.clear();
            mapItems.clear();
        }
    }

}
