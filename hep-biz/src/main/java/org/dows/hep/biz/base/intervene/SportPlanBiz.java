package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.SportPlanInfoResponse;
import org.dows.hep.api.base.intervene.response.SportPlanResponse;
import org.dows.hep.api.base.intervene.vo.SportPlanItemVO;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.dao.SportPlanDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.SportPlanEntity;
import org.dows.hep.entity.SportPlanItemsEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @description project descr:干预:运动方案
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class SportPlanBiz{
    private final SportPlanDao dao;
    private final SportItemDao daoSportItem;

    protected CategCache getPlanCategCache(){
        return CategCacheFactory.SPORTPlan.getCache();
    }

    protected CategCache getItemCategCache(){
        return CategCacheFactory.SPORTItem.getCache();
    }

    /**
    * @param
    * @return
    * @说明: 获取运动方案列表
    * @关联表: sport_plan
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<SportPlanResponse> pageSportPlan(FindSportRequest findSport ) {
        return ShareBiz.buildPage(dao.pageByCondition(findSport),  i->
                CopyWrapper.create(SportPlanResponse::new).endFrom(refreshCateg(i)));

    }
    /**
    * @param
    * @return
    * @说明: 获取运动方案信息
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SportPlanInfoResponse getSportPlan(String appId, String sportPlanId ) {
        SportPlanEntity row= AssertUtil.getNotNull(dao.getById(sportPlanId))
                .orElseThrow("运动方案不存在或已删除，请刷新");
        final CategCache cacheItem= getItemCategCache();

        List<SportPlanItemsEntity> subRows=dao.getSubByLeadId(sportPlanId,
                SportPlanItemsEntity::getId,
                SportPlanItemsEntity::getSportPlanItemsId,
                SportPlanItemsEntity::getSportItemId,
                SportPlanItemsEntity::getSportItemName,
                SportPlanItemsEntity::getFrequency,
                SportPlanItemsEntity::getLastTime,
                SportPlanItemsEntity::getSeq);
        List<String> itemIds=ShareUtil.XCollection.map(subRows,SportPlanItemsEntity::getSportItemId);
        Map<String,SportItemEntity> mapItems=daoSportItem.getMapByIds(itemIds,
                SportItemEntity::getSportItemId,
                SportItemEntity::getInterveneCategId,
                SportItemEntity::getStrengthMet,
                SportItemEntity::getStrengthType);
        List<SportPlanItemVO> vos=ShareUtil.XCollection.map(subRows,i->
                CopyWrapper.create(SportPlanItemVO::new)
                        .endFrom(i,v->v.setRefId(i.getSportPlanItemsId())));
        vos.forEach(i-> {
            SportItemEntity src = mapItems.get(i.getSportItemId());
            if (null == src) {
                return;
            }
            i.setStrengthMet(src.getStrengthMet()).setStrengthType(src.getStrengthType());
            CategVO cacheCateg = cacheItem.getById(appId, src.getInterveneCategId());
            if (null == cacheCateg) {
                return;
            }
            i.setCategIdLv1(cacheCateg.getCategIdLv1()).setCategNameLv1(cacheCateg.getCategNameLv1());
        });
        subRows.clear();
        itemIds.clear();
        mapItems.clear();
        return CopyWrapper.create(SportPlanInfoResponse::new).endFrom(refreshCateg(row))
                .setSportItems(vos);
    }
    /**
    * @param
    * @return
    * @说明: 保存运动方案
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveSportPlan(SaveSportPlanRequest saveSportPlan ) {
        final String appId=saveSportPlan.getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveSportPlan.getSportPlanId())
                        && dao.getById(saveSportPlan.getSportPlanId(), SportPlanEntity::getSportPlanId).isEmpty())
                .throwMessage("运动方案不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveSportPlan.getInterveneCategId())
                        ||null==(categVO= getPlanCategCache().getById(appId,saveSportPlan.getInterveneCategId())))
                .throwMessage("类别不存在");
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveSportPlan.getSportItems())
                &&saveSportPlan.getSportItems().stream()
                .map(SportPlanItemVO::getSportItemId)
                .collect(Collectors.toSet())
                .size()<saveSportPlan.getSportItems().size())
                .throwMessage("存在重复的运动项目，请检查");

        SportPlanEntity row= CopyWrapper.create(SportPlanEntity::new)
                .endFrom(saveSportPlan)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCategIdLv1(categVO.getCategIdLv1())
                .setCategNameLv1(categVO.getCategNameLv1());


        List<SportPlanItemsEntity> subrows=ShareUtil.XCollection.map(saveSportPlan.getSportItems(),
                i->CopyWrapper.create(SportPlanItemsEntity::new)
                        .endFrom(i,v->v.setSportPlanItemsId(i.getRefId())));
        return dao.tranSave(row,subrows,false);
    }
    /**
    * @param
    * @return
    * @说明: 删除运动方案
    * @关联表: sport_plan,sport_plan_items
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delSportPlan(DelSportPlanRequest delSportPlan ) {
        return dao.tranDelete(delSportPlan.getIds(),true);
    }

    /**
     * 删除运动项目
     * @param delRefItem
     * @return
     */
    public Boolean delRefItem(DelRefItemRequest delRefItem ) {
        return dao.tranDeleteSub(delRefItem.getIds(),"运动项目不存在或已删除");
    }
    /**
    * @param
    * @return
    * @说明: 启用、禁用运动方案
    * @关联表: sport_plan
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean setSportPlanState(SetSpotPlanStateRequest setSpotPlanState ) {
        return dao.tranSetState(setSpotPlanState.getSportPlanId(),setSpotPlanState.getState());
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected SportPlanEntity refreshCateg(SportPlanEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getInterveneCategId())) {
            return src;
        }
        CategVO cacheItem = getPlanCategCache().getById(src.getAppId(), src.getInterveneCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath())
                .setCategIdLv1(cacheItem.getCategIdLv1())
                .setCategNameLv1(cacheItem.getCategNameLv1());

    }

}