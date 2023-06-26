package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.base.intervene.response.SportItemResponse;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.IndicatorExpressionRefDao;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.SportItemEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
* @description project descr:干预:运动项目
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class SportItemBiz{

    private final SportItemDao dao;

    private final IndicatorExpressionBiz indicatorExpressionBiz;

    private final IndicatorExpressionRefDao daoExpressionRef;

    protected CategCache getCategCache(){
        return CategCacheFactory.SPORTItem.getCache();
    }
    /**
    * @param
    * @return
    * @说明: 获取运动项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<SportItemResponse> pageSportItem(FindSportRequest findSport ) {
        return ShareBiz.buildPage(dao.pageByCondition(findSport),  i->
                CopyWrapper.create(SportItemResponse::new).endFrom(refreshCateg(i)));

   }
    /**
    * @param
    * @return
    * @说明: 获取运动项目详细信息
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public SportItemInfoResponse getSportItem(String appId, String sportItemId ) {
        SportItemEntity row=AssertUtil.getNotNull(dao.getById(sportItemId))
                .orElseThrow("运动项目不存在");

        List<IndicatorExpressionResponseRs> expressions=ShareBiz.getExpressionsByReasonId(indicatorExpressionBiz,appId,sportItemId);
        return CopyWrapper.create(SportItemInfoResponse::new).endFrom(refreshCateg(row))
                .setExpresssions(expressions);
    }
    /**
    * @param
    * @return
    * @说明: 保存运动项目
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveSportItem(SaveSportItemRequest saveSportItem ) {
        final String appId=saveSportItem.getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveSportItem.getSportItemId())
                        && dao.getById(saveSportItem.getSportItemId(), SportItemEntity::getSportItemId).isEmpty())
                .throwMessage("运动项目不存在");
        CategVO categVO = null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveSportItem.getInterveneCategId())
                        || null == (categVO = getCategCache().getById(appId,saveSportItem.getInterveneCategId())))
                .throwMessage("类别不存在");

        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveSportItem.getExpresssions())
                        && saveSportItem.getExpresssions().stream()
                        .map(IndicatorExpressionVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size() < saveSportItem.getExpresssions().size())
                .throwMessage("存在重复的关联指标，请检查");

        SportItemEntity row = CopyWrapper.create(SportItemEntity::new)
                .endFrom(saveSportItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCategIdLv1(categVO.getCategIdLv1())
                .setCategNameLv1(categVO.getCategNameLv1());

        List<String> expressionIds = new ArrayList<>();
        Optional.ofNullable(saveSportItem.getExpresssions())
                .ifPresent(i -> i.forEach(expression -> expressionIds.add(expression.getIndicatorExpressionId())));

        return dao.tranSaveWithExpressions(row, expressionIds);
    }
    /**
    * @param
    * @return
    * @说明: 删除运动项目
    * @关联表: sport_item,sport_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delSportItem(DelSpotItemRequest delSportItem ) {
        //TODO checkRefence
        return dao.tranDelete(delSportItem.getIds(),true);
    }

    /**
     * 删除关联指标
     * @param delRefIndicator
     * @return
     */
    public Boolean delRefIndicator(DelRefIndicatorRequest delRefIndicator ) {
        return daoExpressionRef.tranDeleteByExpressionId(delRefIndicator.getIds());
    }

    /**
     * 启用,禁用运动项目
     *
     * @param setSportItemStateRequest
     * @return
     */
    public Boolean setSportItemState(SetSportItemStateRequest setSportItemStateRequest ) {

        return dao.tranSetState(setSportItemStateRequest.getSportItemId(), setSportItemStateRequest.getState());
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected SportItemEntity refreshCateg(SportItemEntity src) {
        if (ShareUtil.XObject.isEmpty(src.getInterveneCategId())) {
            return src;
        }
        CategVO cacheItem = getCategCache().getById(src.getAppId(), src.getInterveneCategId());
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