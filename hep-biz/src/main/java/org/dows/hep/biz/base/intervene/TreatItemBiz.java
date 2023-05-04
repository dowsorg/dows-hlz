package org.dows.hep.biz.base.intervene;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelTreatItemRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.request.SaveTreatItemRequest;
import org.dows.hep.api.base.intervene.request.SetTreatItemStateRequest;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.dows.hep.api.base.intervene.vo.InterveneIndicatorVO;
import org.dows.hep.api.enums.EnumStatus;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.IndicatorFuncDao;
import org.dows.hep.biz.dao.TreatItemDao;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.TreatItemEntity;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if(ShareUtil.XString.hasLength(findTreat.getCategIdLv1())) {
            findTreat.setCategIdLv1(ShareUtil.XString.eusureEndsWith(findTreat.getCategIdLv1(),"/"));
        }
        Page<TreatItemEntity> page=Page.of(findTreat.getPageNo(),findTreat.getPageSize());
        page.addOrder(OrderItem.asc("id"));
        page=dao.getByCondition(page,findTreat.getKeywords(), findTreat.getCategIdLv1(),findTreat.getIndicatorFuncId());
        Page<TreatItemResponse> pageDto= Page.of (page.getCurrent(),page.getSize(),page.getTotal(),page.searchCount());
        return pageDto.setRecords(ShareUtil.XCollection.map(page.getRecords(),true, i-> CopyWrapper.create(TreatItemResponse::new)
                .endFrom(i)
                .setCategIdLv1(InterveneCategCache.Instance.getCategLv1(i.getCategIdPath() ,i.getInterveneCategId()))
                .setCategNameLv1(InterveneCategCache.Instance.getCategLv1(i.getCategNamePath() ,i.getCategName()))));

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

        List<TreatItemIndicatorEntity> indicators=dao.getByLeadId(treatItemId,
                TreatItemIndicatorEntity::getId,
                TreatItemIndicatorEntity::getIndicatorInstanceId,
                TreatItemIndicatorEntity::getExpression,
                TreatItemIndicatorEntity::getExpressionDescr,
                TreatItemIndicatorEntity::getSeq);

        List<InterveneIndicatorVO> voIndicators=ShareUtil.XCollection.map(indicators,true,
                i->CopyWrapper.create(InterveneIndicatorVO::new)
                        .endFrom(i)
                        .setRefId(i.getTreatItemIndicatorId()));
        return CopyWrapper.create(TreatItemInfoResponse::new)
                .endFrom(row)
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
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveTreatItem.getId(),true)
                        && dao.getByPk(saveTreatItem.getId(), TreatItemEntity::getId).isEmpty())
                .throwMessage("干预项目不存在");
        CategVO categVO=null;
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(saveTreatItem.getInterveneCategId())
                        ||null==(categVO= InterveneCategCache.Instance.getById(saveTreatItem.getInterveneCategId())))
                .throwMessage("类别不存在");
        final IndicatorFuncEntity funcRow= AssertUtil.getNotNull(indicatorFuncDao.getById(saveTreatItem.getIndicatorFuncId(), IndicatorFuncEntity::getPid))
                .orElseThrow("功能点不存在");

        saveTreatItem.setState(EnumStatus.of(saveTreatItem.getState()).getCode());
        TreatItemEntity row= CopyWrapper.create(TreatItemEntity::new)
                .endFrom(saveTreatItem)
                .setCategName(categVO.getCategName())
                .setCategIdPath(categVO.getCategIdPath())
                .setCategNamePath(categVO.getCategNamePath())
                .setIndicatorCategoryId(funcRow.getPid());

        List<TreatItemIndicatorEntity> rowIndicators=ShareUtil.XCollection.map(saveTreatItem.getIndicators(),true,
                i->CopyWrapper.create(TreatItemIndicatorEntity::new).endFrom(i));
        return dao.tranSave(row,rowIndicators);
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
        return dao.tranDelete(delTreatItem.getIds());
    }

    /**
     * 启用，禁用干预项目
     * @param setTreatItemStateRequest
     * @return
     */
    public Boolean setTreatItemState(SetTreatItemStateRequest setTreatItemStateRequest ){
        setTreatItemStateRequest.setState(EnumStatus.of(setTreatItemStateRequest.getState()).getCode());
        return dao.tranSetState(setTreatItemStateRequest.getTreatItemId(),setTreatItemStateRequest.getState());
    }
}