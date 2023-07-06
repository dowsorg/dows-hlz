package org.dows.hep.biz.base.intervene;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.biz.cache.CategCache;
import org.dows.hep.biz.cache.CategCacheFactory;
import org.dows.hep.biz.dao.EnumCheckCategPolicy;
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

    protected CategCache getCategCache(String family){
        return CategCacheFactory.of(checkFamily(family)).getCache();
    }
    protected CategCache getCategCache(EnumCategFamily family){
        return CategCacheFactory.of(family).getCache();
    }


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
        final String appId=findInterveneCateg.getAppId();
        if(ShareUtil.XObject.allEmpty(findInterveneCateg.getPid(),findInterveneCateg.getFamily())){
            return Collections.emptyList();
        }
        final String pid=ShareUtil.XString.defaultIfEmpty(findInterveneCateg.getPid(),findInterveneCateg.getFamily());
        final CategCache cache=getCategCache(findInterveneCateg.getFamily());
        return cache.getByParentId(appId, pid, Optional.ofNullable(findInterveneCateg.getWithChild()).orElse(0)>0);
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
        final String appId=saveInterveneCateg.getAppId();
        final String pid = saveInterveneCateg.getCategPid();
        final EnumCategFamily enumFamily=checkFamily(saveInterveneCateg.getFamily());
        final CategCache cache=getCategCache(enumFamily);
        AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(saveInterveneCateg.getCategName())
                &&saveInterveneCateg.getCategName().contains(cache.getSplitCategPath()))
                .throwMessage("类别名称不可包含\"/\"符号");

        CategVO parent = ShareUtil.XObject.defaultIfNull(cache.getById(appId, pid),new CategVO());
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
                .setFamily(saveInterveneCateg.getFamily())
                .setCategIdPath(cache.buildCategPath(parent.getCategIdPath(),categId))
                .setCategNamePath(cache.buildCategPath(parent.getCategNamePath(),saveInterveneCateg.getCategName()));
        if(enumFamily==EnumCategFamily.FOODMaterial){
            row.setExtend(JacksonUtil.toJson(saveInterveneCateg.getExtend(), false));
        }
        interveneCategDao.tranSave(row);
        cache.clear();
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
        final String appId=saveInterveneCateg.get(0).getAppId();
        final String pid=saveInterveneCateg.get(0).getCategPid();
        final String family=saveInterveneCateg.get(0).getFamily();
        final EnumCategFamily enumFamily=checkFamily(family);
        final CategCache cache=getCategCache(enumFamily);
        CategVO parent = ShareUtil.XObject.defaultIfNull(cache.getById(appId, pid),new CategVO());
        AssertUtil.trueThenThrow(ShareUtil.XString.hasLength(pid) && ShareUtil.XObject.isEmpty(parent.getCategIdPath()))
                .throwMessage("父类别不存在");
        saveInterveneCateg.forEach(i->
            AssertUtil.trueThenThrow(ShareUtil.XObject.notEmpty(i.getCategName())
                        &&i.getCategName().contains(cache.getSplitCategPath()))
                .throwMessage("类别名称不可包含\"/\"符号"));
        List<InterveneCategoryEntity> rows=ShareUtil.XCollection.map(saveInterveneCateg,i-> {
            if(ShareUtil.XObject.isEmpty(i.getCategId())){
                i.setCategId(idGenerator.nextIdStr());
            }
            return CopyWrapper.create(InterveneCategoryEntity::new)
                    .endFrom(i)
                    .setAppId(appId)
                    .setFamily(family)
                    .setCategPid(pid)
                    .setInterveneCategoryId(i.getCategId())
                    .setCategIdPath(cache.buildCategPath(parent.getCategIdPath(), i.getCategId()))
                    .setCategNamePath(cache.buildCategPath(parent.getCategNamePath(), i.getCategName()));
        });

        interveneCategDao.tranSaveBatch(rows);
        cache.clear();
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
        final String appId= delInterveneCateg.getAppId();
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(delInterveneCateg.getIds()))
                        .throwMessage("缺少必要参数");
        if(ShareUtil.XObject.isEmpty(delInterveneCateg.getFamily())){
            List<InterveneCategoryEntity> rowsCateg= interveneCategDao.getByIds(delInterveneCateg.getAppId(), delInterveneCateg.getIds(),InterveneCategoryEntity::getFamily);
            for(InterveneCategoryEntity row:rowsCateg){
                if(ShareUtil.XObject.notEmpty(row.getFamily() )){
                    delInterveneCateg.setFamily(row.getFamily());
                    break;
                }
            }
        }
        final CategCache cache=getCategCache(delInterveneCateg.getFamily());
        delInterveneCateg.getIds().forEach(i->{
            AssertUtil.trueThenThrow(ShareUtil.XCollection.notEmpty(cache.getByParentId(appId, i,true)))
                    .throwMessage("类别包含子级类别，不可删除");
            CategVO cacheCateg=cache.getById(appId,i);
            AssertUtil.trueThenThrow(null!=cacheCateg&&EnumCheckCategPolicy.checkCategRef(cacheCateg.getFamily(),i))
                    .throwMessage("类别已被引用，不可删除");
        });
        interveneCategDao.tranDelete(delInterveneCateg.getIds());
        cache.clear();
        return true;
    }

    private EnumCategFamily checkFamily(String family){
        AssertUtil.trueThenThrow(ShareUtil.XObject.isEmpty(family))
                .throwMessage("根类别不可为空");
        EnumCategFamily enumFamily=EnumCategFamily.of(family);
        AssertUtil.trueThenThrow(enumFamily==EnumCategFamily.NONE).throwMessage("根类别不存在");
        switch (enumFamily){
            case TreatItem:
                AssertUtil.getNotNull(indicatorFuncDao.getById(ShareUtil.XString.trimStart(family,enumFamily.getCode()), IndicatorFuncEntity::getId))
                        .orElseThrow("指标功能点不存在");
                break;
            default:
                break;
        }
        return enumFamily;

    }
}