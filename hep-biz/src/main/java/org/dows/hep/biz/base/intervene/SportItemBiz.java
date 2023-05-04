package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelSpotItemRequest;
import org.dows.hep.api.base.intervene.request.FindSportRequest;
import org.dows.hep.api.base.intervene.request.SaveSportItemRequest;
import org.dows.hep.api.base.intervene.request.SetSportItemStateRequest;
import org.dows.hep.api.base.intervene.response.SportItemInfoResponse;
import org.dows.hep.api.base.intervene.response.SportItemResponse;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.SportItemDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.SportItemEntity;
import org.dows.hep.entity.SportItemIndicatorEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if(ShareUtil.XString.hasLength(findSport.getCategIdLv1())) {
            findSport.setCategIdLv1(ShareUtil.XString.eusureEndsWith(findSport.getCategIdLv1(),"/"));
        }
        Page<SportItemEntity> page=Page.of(findSport.getPageNo(),findSport.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        page=dao.getByCondition(page,findSport.getKeywords(), findSport.getCategIdLv1());
        Page<SportItemResponse> pageDto= Page.of (page.getCurrent(),page.getSize(),page.getTotal(),page.searchCount());
        return pageDto.setRecords(ShareUtil.XCollection.map(page.getRecords(),true, i-> CopyWrapper.create(SportItemResponse::new)
                .endFrom(i)
                .setCategIdLv1(InterveneCategCache.Instance.getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(InterveneCategCache.Instance.getCategLv1(i.getCategNamePath() ,i.getCategName()))));
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

        List<SportItemIndicatorEntity> indicators=dao.getByLeadId(sportItemId,
                SportItemIndicatorEntity::getId,
                SportItemIndicatorEntity::getIndicatorInstanceId,
                SportItemIndicatorEntity::getExpression,
                SportItemIndicatorEntity::getExpressionDescr,
                SportItemIndicatorEntity::getSeq);

        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,true,
                i->CopyWrapper.create(InterveneIndicatorVO::new)
                        .endFrom(i)
                        .setRefId(i.getSportItemIndicatorId()));
        return CopyWrapper.create(SportItemInfoResponse::new).endFrom(row)
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
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveSportItem.getId(),true)
                        && dao.getByPk(saveSportItem.getId(), SportItemEntity::getId).isEmpty())
                .throwMessage("运动项目不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveSportItem.getInterveneCategId())
                        ||null==(categVO= InterveneCategCache.Instance.getById(saveSportItem.getInterveneCategId())))
                .throwMessage("类别不存在");

        saveSportItem.setState(EnumStatus.of(saveSportItem.getState()).getCode());
        SportItemEntity row= CopyWrapper.create(SportItemEntity::new)
                .endFrom(saveSportItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath());

        List<SportItemIndicatorEntity> rowIndicators=ShareUtil.XCollection.map(saveSportItem.getIndicators(),true,
                i->CopyWrapper.create(SportItemIndicatorEntity::new).endFrom(i));
        return dao.tranSave(row,rowIndicators);
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
        return dao.tranDelete(delSportItem.getIds());
    }

    /**
     * 启用,禁用运动项目
     *
     * @param setSportItemStateRequest
     * @return
     */
    public Boolean setSportItemState(SetSportItemStateRequest setSportItemStateRequest ) {
        setSportItemStateRequest.setState(EnumStatus.of(setSportItemStateRequest.getState()).getCode());
        return dao.tranSetState(setSportItemStateRequest.getSportItemId(), setSportItemStateRequest.getState());
    }
}