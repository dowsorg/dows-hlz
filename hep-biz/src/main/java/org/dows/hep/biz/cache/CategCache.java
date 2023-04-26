package org.dows.hep.biz.cache;

import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : wuzl
 * @date : 2023/4/23 11:30
 */

public abstract class CategCache extends BaseLocalCache<CategCache.CacheData> {

    //region .ctor
    protected final String splitCategPath="/";
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
            if(ShareUtil.XObject.isEmpty(i.getCategPid())){
                i.setCategPid(i.getFamily());
            }
            vCache.mapItems.put(fixKey(i.getCategId()),i);
            vCache.mapGroups.computeIfAbsent(fixKey(i.getCategPid()),v-> new ArrayList<>()).add(i);
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
        CategVO parent = cache.mapItems.get(child.getCategPid());
        if (null == parent) {
            return child;
        }
        sbId.insert(0, String.format("%s%s", parent.getCategId(),splitCategPath));
        sbName.insert(0, String.format("%s%s", parent.getCategName(),splitCategPath));
        return getParent(cache,sbId, sbName, parent);
    }


    //region facade

    /**
     * 按id获取单类别
     * @param categId
     * @return
     */
    public CategVO getById(String categId){
        return ensureCache().mapItems.get(fixKey(categId));
    }

    /**
     * 获取根类别下所有子类别
     * @param family 根类别
     * @param withChild 是否包含子节点
     * @return
     */
    public List<CategVO> getByFamily(EnumCategFamily family,boolean withChild) {
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
        List<CategVO> rst=  ensureCache().mapGroups.get(fixKey(categId));
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
        return ShareUtil.XCollection.map(src,true, i->CategVO.builder()
                .family(i.getFamily())
                .categId(i.getCategId())
                .categPid(i.getCategPid())
                .categName(i.getCategName())
                .categIdPath(i.getCategIdPath())
                .categNamePath(i.getCategNamePath())
                .mark(i.getMark())
                .extend(i.getExtend())
                .seq(i.getSeq())
                .build());
    }


    /**
     * 获取父级路径
     * @param pid
     * @return
     */
    public CategVO getRootPath(String pid){
        if(ShareUtil.XObject.isEmpty(pid)){
            return null;
        }
        CategVO parent=getById(pid);
        if(null==parent){
            return null;
        }
        StringBuilder sbId=new StringBuilder();
        StringBuilder sbName=new StringBuilder();
        if(ShareUtil.XString.hasLength(parent.getCategIdPath())){
            sbId.append(ShareUtil.XString.eusureEndsWith(parent.getCategIdPath(),splitCategPath));
        }
        sbId.append(ShareUtil.XString.eusureEndsWith(parent.getCategId(),splitCategPath));
        if(ShareUtil.XString.hasLength(parent.getCategNamePath())){
            sbName.append(ShareUtil.XString.eusureEndsWith(parent.getCategNamePath(),splitCategPath));
        }
        sbName.append(ShareUtil.XString.eusureEndsWith(parent.getCategName(),splitCategPath));
        return CategVO.builder()
                .categIdPath(sbId.toString())
                .categNamePath(sbName.toString())
                .build();
    }

    public static String getCategLv1(String path,String self){
        if(ShareUtil.XObject.isEmpty(path)){
            return self;
        }
        return path.split("/")[0];
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
        return ShareUtil.XString.defaultIfNull(key,"").toLowerCase();
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
