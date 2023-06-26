package org.dows.hep.biz.cache;


import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/4/23 11:30
 */

public class CategCache extends BaseLocalCache<CategCache.CacheData> implements ICacheClear{

    //region .ctor
    protected final String SPLITCategPath ="/";
    protected final Supplier<List<CategVO>> loadDataFunc;
    protected CategCache(long expireInMinutes,Supplier<List<CategVO>> loadDataFunc){
        super(expireInMinutes);
        this.loadDataFunc=loadDataFunc;
    }
    //endregion

    //region load

    @Override
    protected CacheData load() {
        CacheData vCache=new CacheData();
        List<CategVO> src=loadFromDb();
        if(ShareUtil.XCollection.isEmpty(src)) {
            return vCache;
        }
        src.forEach(i->{
            i.setAppId(Optional.ofNullable(i.getAppId()).orElse(""));
            i.setCategPid(Optional.ofNullable(i.getCategPid()).orElse(""));
            vCache.mapItems.computeIfAbsent(i.getAppId(),k-> new ConcurrentHashMap<>())
                    .put(fixKey(i.getCategId()),i);
            vCache.mapGroups.computeIfAbsent(i.getAppId(),k->new ConcurrentHashMap<>())
                    .computeIfAbsent(fixKey(ShareUtil.XString.defaultIfEmpty(i.getCategPid(),i.getFamily())),v-> new ArrayList<>())
                    .add(i);
        });
        StringBuilder sbId=new StringBuilder();
        StringBuilder sbName=new StringBuilder();
        int[] layer=new int[1];
        src.forEach(i-> {
            //设置父子关联
            i.setChilds(Optional.ofNullable(vCache.mapGroups.get(i.getAppId()))
                    .map(map->map.get(i.getCategId()))
                    .orElse(null)) ;
            sbId.setLength(0);
            sbName.setLength(0);
            layer[0]=0;
            CategVO parent = getParent(vCache, sbId, sbName, i,layer);
            i.setCategIdPath(sbId.toString())
                    .setCategNamePath(sbName.toString())
                    .setFamily(parent.getFamily())
                    .setCategIdLv1(parent.getCategId())
                    .setCategNameLv1(parent.getCategName())
                    .setLayer(layer[0]);
        });
        src.clear();
        //TODO 分布式处理
        return vCache;
    }
    //填充父级路径
    protected CategVO getParent(CacheData cache, StringBuilder sbId, StringBuilder sbName, CategVO child,int[] layer) {

        if (ShareUtil.XObject.isEmpty(child)) {
            return null;
        }
        sbId.insert(0, String.format("%s%s", child.getCategId(), SPLITCategPath));
        sbName.insert(0, String.format("%s%s", child.getCategName(), SPLITCategPath));
        layer[0]++;
        CategVO parent =Optional.ofNullable(cache.mapItems.get(child.getAppId()))
                .map(map->map.get(child.getCategPid()))
                .orElse(null) ;
        if (null == parent) {
            return child;
        }
        return getParent(cache,sbId, sbName, parent,layer);
    }
    protected Optional<Map<String,List<CategVO>>> getMapGroup(String appId){
        return Optional.ofNullable( ensureCache().mapGroups.get(appId));
    }
    protected Optional<Map<String,CategVO>> getMapItems(String appId){
        return Optional.ofNullable( ensureCache().mapItems.get(appId));
    }
    //endregion


    //region facade
    public Collection<CategVO> getAll(String appId){
        return getMapItems(appId)
                .map(i->i.values())
                .orElse(new ArrayList<>());
    }

    /**
     * 按id获取单类别
     * @param appId
     * @param categId
     * @return
     */
    public CategVO getById(String appId, String categId){
        if(ShareUtil.XObject.isEmpty(categId)){
            return null;
        }
        return getMapItems(appId)
                .map(map->map.get(fixKey(categId)))
                .orElse(null);
    }


    /**
     * 获取父id下所有子类别
     * @param categId 父类别
     * @param withChild 是否包含子节点
     * @return
     */
    public List<CategVO> getByParentId(String appId, String categId,boolean withChild) {
        if(ShareUtil.XObject.isEmpty(categId)){
            return Collections.emptyList();
        }
        List<CategVO> rst=  getMapGroup(appId)
                .map(map->map.get(fixKey(categId)))
                .orElse(new ArrayList<>());
        return withChild?rst:flatCopy(rst);
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
     * 设置子级目录路径
     *
     * @param path
     * @param self
     * @return
     */
    public String buildCategPath(String path, String self){
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

    protected List<CategVO> loadFromDb(){
        if(null==this.loadDataFunc){
            return Collections.emptyList();
        }
        return loadDataFunc.get();
    }
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
        protected final Map<String,Map<String, List<CategVO>>> mapGroups=new ConcurrentHashMap<>();
        protected final Map<String,Map<String,  CategVO>> mapItems=new ConcurrentHashMap<>();


        protected void clear(){
            for (Map<String,  CategVO> map:  mapItems.values()) {
                map.values().forEach(i -> i.setChilds(null));
            }
            mapGroups.clear();
            mapItems.clear();
        }
    }

}
