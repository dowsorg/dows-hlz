package org.dows.hep.biz.cache;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.dao.EventCategDao;
import org.dows.hep.biz.dao.InterveneCategDao;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.entity.InterveneCategoryEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/6/16 16:37
 */
@Slf4j
public enum CategCacheFactory {
    NONE(EnumCategFamily.NONE,0),
    FOODMaterial(EnumCategFamily.FOODMaterial,60),
    FOODDishes(EnumCategFamily.FOODDishes,60),
    FOODCookBook(EnumCategFamily.FOODCookBook,60),

    SPORTItem(EnumCategFamily.SPORTItem,60),
    SPORTPlan(EnumCategFamily.SPORTPlan,60),

    TreatItem(EnumCategFamily.TreatItem,60),

    EVENT(EnumCategFamily.EVENT,60),
    ;
    private EnumCategFamily enumCategFamily;


    private volatile CategCache categCache;
    CategCacheFactory(EnumCategFamily enumCategFamily, long expireInMinutes) {
        this.enumCategFamily=enumCategFamily;
        this.categCache=new CategCache(expireInMinutes,this::loadCacheData);
    }

    public EnumCategFamily getFamily(){
        return this.enumCategFamily;
    }
    public CategCache getCache(){
        return this.categCache;
    }

    public static CategCacheFactory of(String family){
        return of(EnumCategFamily.of(family));
    }
    public static CategCacheFactory of(EnumCategFamily family){
        if(null==family){
            return CategCacheFactory.NONE;
        }
        for(CategCacheFactory item: CategCacheFactory.values()){
            if(item.getFamily().equals(family)){
                return item;
            }
        }
        return CategCacheFactory.NONE;
    }

    /**
     * 获取关键饮食分类
     * @return
     */
    public static List<CategVO> getPrimeCategs(String appId){
        return CategCacheFactory.FOODMaterial.getCache().getAll(appId).stream()
                .filter(i->Integer.valueOf(1).equals( i.getMark()))
                .sorted(Comparator.comparingInt((CategVO a) -> Optional.ofNullable(a.getSeq()).orElse(0)).thenComparingLong(CategVO::getId))
                .collect(Collectors.toList());
    }



    //region loadData
    private List<CategVO> loadCacheData(){
        switch (this.enumCategFamily){
            case NONE:
                return Collections.emptyList();
            case EVENT:
                 return loadEventData();
            default:
                return loadInterveneData(this.enumCategFamily);
        }
    }

    private static List<CategVO> loadInterveneData(EnumCategFamily family){
        InterveneCategDao dao= CrudContextHolder.getBean(InterveneCategDao.class);
        List<InterveneCategoryEntity> rows=dao.getByFamily(family);
        if(ShareUtil.XCollection.isEmpty(rows)) {
            return Collections.emptyList();
        }
        List<CategVO> rst=new ArrayList<>();
        rows.forEach(i->{
            CategVO vo= CopyWrapper.create(CategVO::new).endFrom(i).setCategId(i.getInterveneCategoryId());
            rst.add(vo);
            if(family!=EnumCategFamily.FOODMaterial||ShareUtil.XObject.isEmpty(i.getExtend())) {
                return;
            }
            try{
                vo.setExtend(JacksonUtil.fromJson(i.getExtend(), FoodCategExtendVO.class));
            }catch (Exception ex){
                log.error(String.format("EnumCategCache.loadInterveneData err. id:%s extend:%s",i.getId(), i.getExtend()) ,ex);
            }
        });
        return rst;
    }

    private static List<CategVO> loadEventData() {
        EventCategDao dao= CrudContextHolder.getBean(EventCategDao.class);
        List<EventCategEntity> rows=dao.getAll();
        if(ShareUtil.XCollection.isEmpty(rows)) {
            return Collections.emptyList();
        }
        List<CategVO> rst=new ArrayList<>();
        rows.forEach(i->rst.add(CopyWrapper.create(CategVO::new).endFrom(i).setCategId(i.getEventCategId())));
        return rst;
    }
    //endregion



}
