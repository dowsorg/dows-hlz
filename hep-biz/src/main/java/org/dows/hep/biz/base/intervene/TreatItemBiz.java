package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.intervene.request.*;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.dows.hep.api.base.intervene.vo.IndicatorExpressionVO;
import org.dows.hep.api.enums.EnumExptOperateType;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.IndicatorExpressionRefDao;
import org.dows.hep.biz.dao.IndicatorFuncDao;
import org.dows.hep.biz.dao.TreatItemDao;
import org.dows.hep.biz.snapshot.SnapshotRequestHolder;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.TreatItemEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private final IndicatorExpressionRefDao daoExpressionRef;
    private final IndicatorExpressionBiz indicatorExpressionBiz;

    protected CategCache getCategCache(){
        if(SnapshotRequestHolder.hasSnapshotRequest()){
            return CategCacheFactory.TreatItem.getExptCache();
        }
        return CategCacheFactory.TreatItem.getCache();
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
        return ShareBiz.buildPage(dao.pageByCondition(findTreat), i ->
                CopyWrapper.create(TreatItemResponse::new).endFrom(refreshCateg(i)));

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
    public TreatItemInfoResponse infoTreatItem(String appId, String treatItemId ) {
        TreatItemEntity row=AssertUtil.getNotNull(dao.getById(treatItemId))
                .orElseThrow("干预项目不存在");
        List<IndicatorExpressionResponseRs> expressions=ShareBiz.getExpressionsByReasonId(indicatorExpressionBiz,appId,treatItemId);
        return CopyWrapper.create(TreatItemInfoResponse::new)
                .endFrom(refreshCateg(row))
                .setExpresssions(expressions);
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
        final String appId=saveTreatItem.getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveTreatItem.getTreatItemId())
                        && dao.getById(saveTreatItem.getTreatItemId(), TreatItemEntity::getTreatItemId).isEmpty())
                .throwMessage("干预项目不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveTreatItem.getInterveneCategId())
                        ||null==(categVO= getCategCache().getById(appId,saveTreatItem.getInterveneCategId())))
                .throwMessage("类别不存在");
        final IndicatorFuncEntity funcRow= AssertUtil.getNotNull(indicatorFuncDao.getById(saveTreatItem.getIndicatorFuncId(),
                        IndicatorFuncEntity::getPid,
                        IndicatorFuncEntity::getIndicatorCategoryId))
                .orElseThrow("功能点不存在");
        EnumExptOperateType operateType=EnumExptOperateType.ofCategId(funcRow.getIndicatorCategoryId());
        AssertUtil.trueThenThrow(operateType==EnumExptOperateType.NONE)
                .throwMessage("未知的功能点类别");
        AssertUtil.trueThenThrow(operateType.getCategLayer()>0&&!categVO.getLayer().equals(operateType.getCategLayer()))
                .throwMessage(String.format("请选择到第%s级类别", operateType.getCategLayer()));
        AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(saveTreatItem.getExpresssions())
                        &&saveTreatItem.getExpresssions().stream()
                        .map(IndicatorExpressionVO::getIndicatorInstanceId)
                        .collect(Collectors.toSet())
                        .size()<saveTreatItem.getExpresssions().size())
                .throwMessage("存在重复的关联指标，请检查");


        TreatItemEntity row= CopyWrapper.create(TreatItemEntity::new)
                .endFrom(saveTreatItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setCategIdLv1(categVO.getCategIdLv1())
                .setCategNameLv1(categVO.getCategNameLv1())
                .setIndicatorCategoryId(funcRow.getIndicatorCategoryId());

        List<String> expressionIds = new ArrayList<>();
        Optional.ofNullable(saveTreatItem.getExpresssions())
                .ifPresent(i -> i.forEach(expression -> expressionIds.add(expression.getIndicatorExpressionId())));
        return dao.tranSaveWithExpressions(row, expressionIds);
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
        return daoExpressionRef.tranDeleteByExpressionId(delRefIndicator.getIds());
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