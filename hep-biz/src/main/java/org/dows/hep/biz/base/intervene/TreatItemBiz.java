package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.IndicatorFuncDao;
import org.dows.hep.biz.dao.TreatItemDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
* @description project descr:干预:治疗项目
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
public class TreatItemBiz{

    private final TreatItemDao dao;
    private final IndicatorFuncDao indicatorFuncDao;

    protected InterveneCategCache getCategCache(){
        return InterveneCategCache.Instance;
    }
    /**
    * @param
    * @return
    * @说明: 获取治疗项目列表
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Page<TreatItemResponse> pageTreatItem(FindTreatRequest findTreat ) {
        findTreat.setCategIdLv1(ShareBiz.ensureCategPathSuffix(findTreat.getCategIdLv1()));
        return ShareBiz.buildPage(dao.pageByCondition(findTreat), i -> CopyWrapper.create(TreatItemResponse::new)
                .endFrom(refreshCateg(i))
                .setCategIdLv1(getCategCache().getCategLv1(i.getCategIdPath(), i.getInterveneCategId()))
                .setCategNameLv1(getCategCache().getCategLv1(i.getCategNamePath(), i.getCategName())));

    }

    /**
    * @param
    * @return
    * @说明: 获取治疗项目信息
    * @关联表: treat_item,treat_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public TreatItemInfoResponse infoTreatItem(String treatItemId ) {
        TreatItemEntity row=AssertUtil.getNotNull(dao.getById(treatItemId))
                .orElseThrow("干预项目不存在");

        List<TreatItemIndicatorEntity> indicators=dao.getSubByLeadId(treatItemId,
                TreatItemIndicatorEntity::getId,
                TreatItemIndicatorEntity::getIndicatorInstanceId,
                TreatItemIndicatorEntity::getExpression,
                TreatItemIndicatorEntity::getExpressionDescr,
                TreatItemIndicatorEntity::getSeq);

        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,
                i->CopyWrapper.create(InterveneIndicatorVO::new)
                        .endFrom(i,v->v.setRefId(i.getTreatItemIndicatorId())));
        return CopyWrapper.create(TreatItemInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setIndicators(voIndicators);
    }
    /**
    * @param
    * @return
    * @说明: 保存治疗项目
    * @关联表: treat_item,treat_item_indicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveTreatItem(SaveTreatItemRequest saveTreatItem ) {
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveTreatItem.getTreatItemId())
                        && dao.getById(saveTreatItem.getTreatItemId(), TreatItemEntity::getTreatItemId).isEmpty())
                .throwMessage("干预项目不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveTreatItem.getInterveneCategId())
                        ||null==(categVO= getCategCache().getById(saveTreatItem.getInterveneCategId())))
                .throwMessage("类别不存在");
        final IndicatorFuncEntity funcRow= AssertUtil.getNotNull(indicatorFuncDao.getById(saveTreatItem.getIndicatorFuncId(), IndicatorFuncEntity::getPid))
                .orElseThrow("功能点不存在");
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveTreatItem.getIndicators())
                        &&saveTreatItem.getIndicators().stream()
                        .map(InterveneIndicatorVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size()<saveTreatItem.getIndicators().size())
                .throwMessage("存在重复的关联指标，请检查");

        TreatItemEntity row= CopyWrapper.create(TreatItemEntity::new)
                .endFrom(saveTreatItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setIndicatorCategoryId(funcRow.getPid());

        List<TreatItemIndicatorEntity> subRows=ShareUtil.XCollection.map(saveTreatItem.getIndicators(),
                i->CopyWrapper.create(TreatItemIndicatorEntity::new).endFrom(i,v->v.setTreatItemIndicatorId(i.getRefId())));
        return dao.tranSave(row,subRows,false);
    }
    /**
    * @param
    * @return
    * @说明: 删除治疗项目
    * @关联表: 
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delTreatItem(DelTreatItemRequest delTreatItem ) {
        return dao.tranDelete(delTreatItem.getIds(),true);
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
     * 启用，禁用干预项目
     * @param setTreatItemStateRequest
     * @return
     */
    public Boolean setTreatItemState(SetTreatItemStateRequest setTreatItemStateRequest ){
        return dao.tranSetState(setTreatItemStateRequest.getTreatItemId(),setTreatItemStateRequest.getState());
    }

    /**
     * 获取缓存最新分类信息
     * @param src
     * @return
     */
    protected TreatItemEntity refreshCateg(TreatItemEntity src) {
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