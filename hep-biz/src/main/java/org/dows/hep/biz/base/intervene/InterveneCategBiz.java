package org.dows.hep.biz.base.intervene;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.biz.cache.InterveneCategCache;
import org.dows.hep.biz.dao.InterveneCategDao;
import org.dows.hep.biz.enums.EnumCategFamily;
import org.dows.hep.biz.util.AssertUtil;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.InterveneCategoryEntity;
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
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(findInterveneCateg.getFamily())&&null== EnumCategFamily.of(findInterveneCateg.getFamily()))
                .throwMessage("根类别不存在");
        final String pid=ShareUtil.XString.defaultIfNull(findInterveneCateg.getPid(),findInterveneCateg.getFamily());
        return InterveneCategCache.Instance.getByParentId(pid, Optional.ofNullable(findInterveneCateg.getWithChild()).orElse(false));
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
        final String categId = saveInterveneCateg.getCategId();
        final String pid = saveInterveneCateg.getCategPid();
        final String family=ShareUtil.XString.hasLength(pid)?"":saveInterveneCateg.getFamily();
        saveInterveneCateg.setFamily(family);
        AssertUtil.trueThenThrow(ShareUtil.XObject.isAllEmpty(family,pid))
                .throwMessage("未找到父类别参数");
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(family) && null == EnumCategFamily.of(family))
                .throwMessage("根类别不存在");
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(categId) && null == InterveneCategCache.Instance.getById(categId))
                .throwMessage("类别不存在");
        CategVO root = InterveneCategCache.Instance.getRootPath(pid);
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && null == root)
                .throwMessage("父类别不存在");
        InterveneCategoryEntity row = CopyWrapper.create(InterveneCategoryEntity::new).endFrom(saveInterveneCateg)
                .setInterveneCategoryId(categId)
                .setExtend(JacksonUtil.toJson(saveInterveneCateg.getExtend(), false));
        if (null != root) {
            row.setCategIdPath(root.getCategIdPath()).setCategNamePath(root.getCategNamePath());
        }
        AssertUtil.falseThenThrow(interveneCategDao.saveOrUpdate(row))
                .throwMessage("保存失败");
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
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(family) && null == EnumCategFamily.of(family))
                .throwMessage("根类别不存在");
        CategVO root = ShareUtil.XObject.defaultIfNull(InterveneCategCache.Instance.getRootPath(pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(root.getCategIdPath()))
                .throwMessage("父类别不存在");
        List<InterveneCategoryEntity> rows=ShareUtil.XCollection.map(saveInterveneCateg,true,i->CopyWrapper.create(InterveneCategoryEntity::new)
                .endFrom(i)
                .setFamily(family)
                .setCategPid(pid)
                .setCategIdPath(root.getCategIdPath())
                .setCategNamePath(root.getCategNamePath()));
        AssertUtil.falseThenThrow(interveneCategDao.saveOrUpdate(rows))
                .throwMessage("保存失败");
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
            AssertUtil.trueThenThrow(!ShareUtil.XCollection.isEmpty(childs))
                    .throwMessage("包含子级类别不可删除，请检查");
        });
        AssertUtil.falseThenThrow(interveneCategDao.delByIds(delInterveneCateg.getIds()))
                        .throwMessage("删除失败");
        InterveneCategCache.Instance.clear();
        return true;
    }
}