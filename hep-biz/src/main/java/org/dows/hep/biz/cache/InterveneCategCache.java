package org.dows.hep.biz.cache;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;
import org.dows.hep.api.enums.EnumCategFamily;
import org.dows.hep.biz.dao.InterveneCategDao;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.InterveneCategoryEntity;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 *
 * @author : wuzl
 * @date : 2023/4/21 16:33
 */
@Slf4j
public class InterveneCategCache extends CategCache {

    //region instance
    public static final InterveneCategCache Instance =new InterveneCategCache();
    private static final long EXPIREInMinutes=60;

    private InterveneCategCache(){
        super(EXPIREInMinutes);
    }
    //endregion



    @Override
    protected List<CategVO> loadFromDb() {
        InterveneCategDao dao=CrudContextHolder.getBean(InterveneCategDao.class);
        List<InterveneCategoryEntity> rows=dao.getAll();
        if(ShareUtil.XCollection.isEmpty(rows)) {
            return Collections.emptyList();
        }
        List<CategVO> rst=new ArrayList<>();
        rows.forEach(i->{
            CategVO vo=CopyWrapper.create(CategVO::new).endFrom(i).setCategId(i.getInterveneCategoryId());
            try{
                vo.setExtend(JacksonUtil.fromJson(i.getExtend(), FoodCategExtendVO.class));
            }catch (Exception ex){
                log.error(String.format("InterveneCategCache.load err. id:%s extend:%s",i.getId(), i.getExtend()) ,ex);
            }
            rst.add(vo);
        });
        return rst;
    }

    /**
     * 获取关键饮食分类
     * @return
     */
    public List<CategVO> getPrimeCategs(){
        return ensureCache().mapItems.values().stream()
                .filter(i->Integer.valueOf(1).equals( i.getMark())&& EnumCategFamily.FOODMaterial.getCode().equalsIgnoreCase(i.getFamily()))
                .sorted(Comparator.comparingInt((CategVO a) -> Optional.ofNullable(a.getSeq()).orElse(0)).thenComparingLong(CategVO::getId))
                .collect(Collectors.toList());
    }


}
