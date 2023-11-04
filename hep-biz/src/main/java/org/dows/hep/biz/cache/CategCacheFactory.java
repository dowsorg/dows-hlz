package org.dows.hep.biz.cache;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.dao.EventCategDao;
import org.dows.hep.biz.dao.InterveneCategDao;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.dao.TreatItemDao;
import org.dows.hep.biz.snapshot.EnumSnapshotType;
import org.dows.hep.biz.snapshot.SnapshotRefCache;
import org.dows.hep.biz.snapshot.SnapshotRequestHolder;
import org.dows.hep.biz.util.BigDecimalUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.EventCategEntity;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.TreatItemEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/6/16 16:37
 */
@Slf4j
public enum CategCacheFactory {
    NONE(EnumCategFamily.NONE,EnumSnapshotType.NONE, 0),
    FOODMaterial(EnumCategFamily.FOODMaterial,EnumSnapshotType.CATEGIntervene, 60),
    FOODDishes(EnumCategFamily.FOODDishes,EnumSnapshotType.CATEGIntervene, 60),
    FOODCookBook(EnumCategFamily.FOODCookBook,EnumSnapshotType.CATEGIntervene, 60),

    SPORTItem(EnumCategFamily.SPORTItem,EnumSnapshotType.CATEGIntervene, 60){
        @Override
        protected List<CategVO> loadExptCacheData() {
            List<CategVO> rst= super.loadExptCacheData();
            if(ShareUtil.XObject.isEmpty(rst)){
                return rst;
            }
            final String appId=rst.get(0).getAppId();
            rst.addAll(ShareUtil.XCollection.map(CrudContextHolder.getBean(SportItemDao.class)
                    .listByCondition(new FindSportRequest(),
                    SportItemEntity::getStrengthMet,
                    SportItemEntity::getInterveneCategId,
                    SportItemEntity::getSportItemId,
                    SportItemEntity::getSportItemName),row->CategVO.builder()
                    .categId(row.getSportItemId())
                    .categName(row.getSportItemName())
                    .categPid(row.getInterveneCategId())
                    .appId(appId)
                    .spec(BigDecimalUtil.formatRoundDecimal( row.getStrengthMet(),2))
                    .build()));
            return rst;
        }
    },
    SPORTPlan(EnumCategFamily.SPORTPlan,EnumSnapshotType.CATEGIntervene, 60),

    TreatItem(EnumCategFamily.TreatItem,EnumSnapshotType.CATEGIntervene, 60){
        @Override
        protected List<CategVO> loadExptCacheData() {
            List<CategVO> rst= super.loadExptCacheData();
            if(ShareUtil.XObject.isEmpty(rst)){
                return rst;
            }
            final String appId=rst.get(0).getAppId();
            rst.addAll(ShareUtil.XCollection.map(CrudContextHolder.getBean(TreatItemDao.class)
                    .getByIndicatorFuncId(appId,null,
                            TreatItemEntity::getTreatItemId,
                            TreatItemEntity::getTreatItemName,
                            TreatItemEntity::getInterveneCategId,
                            TreatItemEntity::getMinWeight,
                            TreatItemEntity::getMaxWeight),row->CategVO.builder()
                    .categId(row.getTreatItemId())
                    .categName(row.getTreatItemName())
                    .categPid(row.getInterveneCategId())
                    .extend(new FoodCategExtendVO()
                            .setMin(BigDecimalUtil.formatDecimal(row.getMinWeight(), MINWeight4TreatItem))
                            .setMax(BigDecimalUtil.formatDecimal(row.getMaxWeight(), MAXWeight4TreatItem)))
                    .appId(appId)
                    .build()));
            return rst;
        }
    },

    EVENT(EnumCategFamily.EVENT,EnumSnapshotType.CATEGEvent, 60),
    ;
    private EnumCategFamily categFamily;

    private EnumSnapshotType snapshotType;


    private volatile CategCache categCache;

    private volatile ExperimentCategCache exptCategCache;

    private final static String MAXWeight4TreatItem="1000";
    private final static String MINWeight4TreatItem="0";


    CategCacheFactory(EnumCategFamily categFamily,EnumSnapshotType snapshotType, long expireInMinutes) {
        this.categFamily =categFamily;
        this.snapshotType=snapshotType;
        this.categCache=new CategCache(expireInMinutes,this::loadCacheData);
        this.exptCategCache=new ExperimentCategCache(this::loadExptCacheData);
    }

    public EnumCategFamily getFamily(){
        return this.categFamily;
    }
    public CategCache getCache(){
        return this.categCache;
    }

    public CategCache getExptCache(){
        String refExperimentId = SnapshotRequestHolder.getRefExperimentId(this.snapshotType);
        return exptCategCache.loadingCache().get(refExperimentId);
    }
    public CategCache getExptCache(String appId, String experimentId) {
        String refExperimentId = SnapshotRefCache.Instance().getRefExperimentId(appId, snapshotType, experimentId);
        return exptCategCache.loadingCache().get(refExperimentId);
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
        CategCache cache=SnapshotRequestHolder.hasSnapshotRequest()?
                CategCacheFactory.FOODMaterial.getExptCache(): CategCacheFactory.FOODMaterial.getCache();
        return cache.getAll(appId).stream()
                .filter(i->Integer.valueOf(1).equals( i.getMark()))
                .sorted(Comparator.comparingInt((CategVO a) -> Optional.ofNullable(a.getSeq()).orElse(0)).thenComparingLong(CategVO::getId))
                .collect(Collectors.toList());
    }



    //region loadData
    protected List<CategVO> loadCacheData(){
        switch (this.categFamily){
            case NONE:
                return Collections.emptyList();
            case EVENT:
                 return loadEventData();
            default:
                return loadInterveneData(this.categFamily);
        }
    }
    protected List<CategVO> loadExptCacheData(){
        return loadCacheData();
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
