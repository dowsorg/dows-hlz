package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.base.intervene.response.SportItemResponse;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.SportItemIndicatorEntity;
import org.springframework.stereotype.Service;

import java.util.List;
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

    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
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
        findSport.setCategIdLv1(ShareBiz.ensureCategPathSuffix(findSport.getCategIdLv1()));
        return ShareBiz.buildPage(dao.pageByCondition(findSport),  i-> CopyWrapper.create(SportItemResponse::new)
                .endFrom(refreshCateg(i))
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath() ,i.getCategName())));

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
    public SportItemInfoResponse getSportItem(String sportItemId ) {
        SportItemEntity row=AssertUtil.getNotNull(dao.getById(sportItemId))
                .orElseThrow("运动项目不存在");

        List<SportItemIndicatorEntity> indicators=dao.getSubByLeadId(sportItemId,
                SportItemIndicatorEntity::getId,
                SportItemIndicatorEntity::getIndicatorInstanceId,
                SportItemIndicatorEntity::getExpression,
                SportItemIndicatorEntity::getExpressionDescr,
                SportItemIndicatorEntity::getSeq);

        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,
                i->CopyWrapper.create(InterveneIndicatorVO::new)
                        .endFrom(i,v->v.setRefId(i.getSportItemIndicatorId())));
        return CopyWrapper.create(SportItemInfoResponse::new).endFrom(refreshCateg(row))
                .setIndicators(voIndicators);

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
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveSportItem.getSportItemId())
                        && dao.getById(saveSportItem.getSportItemId(), SportItemEntity::getSportItemId).isEmpty())
                .throwMessage("运动项目不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveSportItem.getInterveneCategId())
                        ||null==(categVO= getCategCache().getById(saveSportItem.getInterveneCategId())))
                .throwMessage("类别不存在");

        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveSportItem.getIndicators())
                        &&saveSportItem.getIndicators().stream()
                        .map(InterveneIndicatorVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size()<saveSportItem.getIndicators().size())
                .throwMessage("存在重复的关联指标，请检查");

        SportItemEntity row= CopyWrapper.create(SportItemEntity::new)
                .endFrom(saveSportItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());

        List<SportItemIndicatorEntity> subRows=ShareUtil.XCollection.map(saveSportItem.getIndicators(),
                i->CopyWrapper.create(SportItemIndicatorEntity::new).endFrom(i,v->v.setSportItemIndicatorId(i.getRefId())));
        return dao.tranSave(row,subRows,false);
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
        return dao.tranDeleteSub(delRefIndicator.getIds(),"关联指标不存在或已删除");
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
        CategVO cacheItem = getCategCache().getById(src.getInterveneCategId());
        if (null == cacheItem) {
            return src;
        }
        return src.setCategName(cacheItem.getCategName())
                .setCategIdPath(cacheItem.getCategIdPath())
                .setCategNamePath(cacheItem.getCategNamePath());

    }
}