package org.dows.hep.biz.base.intervene;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.IndicatorFuncDao;
import org.dows.hep.biz.dao.InterveneCategDao;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.IndicatorFuncEntity;
import org.dows.hep.entity.InterveneCategoryEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class InterveneCategBiz {

    private final InterveneCategDao interveneCategDao;
    private final IndicatorFuncDao indicatorFuncDao;

    private final IdGenerator idGenerator;

    /**
    * @param
    * @return
    * @说明: 获取类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public List<CategVO> listInterveneCateg(FindInterveneCategRequest findInterveneCateg ) {
        if(ShareUtil.XObject.isAllEmpty(findInterveneCateg.getPid(),findInterveneCateg.getFamily())){
            return Collections.emptyList();
        }
        final String family=findInterveneCateg.getFamily();
        checkFamily(family);
        final String pid=ShareUtil.XString.defaultIfNull(findInterveneCateg.getPid(),findInterveneCateg.getFamily());
        return InterveneCategCache.Instance.getByParentId(pid, Optional.ofNullable(findInterveneCateg.getWithChild()).orElse(0)>0);
    }
    /**
    * @param
    * @return
    * @说明: 保存类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveInterveneCateg(SaveInterveneCategRequest saveInterveneCateg ) throws JsonProcessingException {
        final String pid = saveInterveneCateg.getCategPid();
        final String family=ShareUtil.XString.hasLength(pid)?"":saveInterveneCateg.getFamily();
        saveInterveneCateg.setFamily(family);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isAllEmpty(family,pid))
                .throwMessage("未找到父类别参数");
        checkFamily(family);
        CategVO parent = ShareUtil.XObject.defaultIfNull(InterveneCategCache.Instance.getById(pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(parent.getCategIdPath()))
                .throwMessage("父类别不存在");
        if(ShareUtil.XObject.isEmpty(saveInterveneCateg.getCategId())){
            saveInterveneCateg.setCategId(idGenerator.nextIdStr());
        } else{
            AssertUtil.getNotNull(interveneCategDao.getById(saveInterveneCateg.getCategId(),InterveneCategoryEntity::getId))
                    .orElseThrow("类别不存在");
        }
        final String categId = saveInterveneCateg.getCategId();
        InterveneCategoryEntity row = CopyWrapper.create(InterveneCategoryEntity::new).endFrom(saveInterveneCateg)
                .setInterveneCategoryId(categId)
                .setExtend(JacksonUtil.toJson(saveInterveneCateg.getExtend(), false))
                .setCategIdPath(InterveneCategCache.Instance.getCategPath(parent.getCategIdPath(),categId))
                .setCategNamePath(InterveneCategCache.Instance.getCategPath(parent.getCategNamePath(),saveInterveneCateg.getCategName()));

        interveneCategDao.tranSave(row);
        InterveneCategCache.Instance.clear();
        return true;
    }
    /**
    * @param
    * @return
    * @说明: 批量保存类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean saveInterveneCategs(List<SaveInterveneCategRequest> saveInterveneCateg ) throws JsonProcessingException {
        if(ShareUtil.XObject.isEmpty(saveInterveneCateg)){
            return false;
        }
        final String pid=saveInterveneCateg.get(0).getCategPid();
        final String family=ShareUtil.XString.hasLength(pid)?"":saveInterveneCateg.get(0).getFamily();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isAllEmpty(family,pid))
                .throwMessage("未找到父类别参数");
        checkFamily(family);

        CategVO parent = ShareUtil.XObject.defaultIfNull(InterveneCategCache.Instance.getById(pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(parent.getCategIdPath()))
                .throwMessage("父类别不存在");
        List<InterveneCategoryEntity> rows=ShareUtil.XCollection.map(saveInterveneCateg,true,i-> {
            if(ShareUtil.XObject.isEmpty(i.getCategId())){
                i.setCategId(idGenerator.nextIdStr());
            }
            return CopyWrapper.create(InterveneCategoryEntity::new)
                    .endFrom(i)
                    .setFamily(family)
                    .setInterveneCategoryId(i.getCategId())
                    .setCategPid(pid)
                    .setCategIdPath(InterveneCategCache.Instance.getCategPath(parent.getCategIdPath(), i.getCategId()))
                    .setCategNamePath(InterveneCategCache.Instance.getCategPath(parent.getCategNamePath(), i.getCategName()));
        });

        interveneCategDao.tranSave(rows);
        InterveneCategCache.Instance.clear();
        return true;
    }
    /**
    * @param
    * @return
    * @说明: 删除类别
    * @关联表: intervene_category
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月23日 上午9:44:34
    */
    public Boolean delInterveneCateg(DelInterveneCategRequest delInterveneCateg ) {
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(delInterveneCateg.getIds()))
                        .throwMessage("缺少必要参数");
        delInterveneCateg.getIds().forEach(i->{
            List<CategVO> childs=InterveneCategCache.Instance.getByParentId(i,true);
            AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(childs))
                    .throwMessage("包含子级类别不可删除，请检查");
        });
        interveneCategDao.transDelete(delInterveneCateg.getIds());
        InterveneCategCache.Instance.clear();
        return true;
    }

    private boolean checkFamily(String family){
        if(ShareUtil.XObject.isEmpty(family)||null!=EnumCategFamily.of(family)) {
            return true;
        }
        AssertUtil.getNotNull(indicatorFuncDao.getById(family, IndicatorFuncEntity::getId)).orElseThrow("根类别或功能点不存在");
        return true;

    }
}