package org.dows.hep.biz.cache;

import org.dows.framework.crud.api.CrudContextHolder;
import org.dows.hep.biz.dao.EventCategDao;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.biz.vo.CategVO;
import org.dows.hep.entity.EventCategEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/4/23 13:27
 */
public class EventCategCache extends CategCache{

    //region instance
    public static final EventCategCache Instance =new EventCategCache();
    private static final long EXPIREInMinutes=60;

    private EventCategCache(){
        super(EXPIREInMinutes);
    }
    //endregion

    @Override
    protected List<CategVO> loadFromDb() {
        EventCategDao dao= CrudContextHolder.getBean(EventCategDao.class);
        List<EventCategEntity> rows=dao.getAll();
        if(ShareUtil.XCollection.isEmpty(rows)) {
            return Collections.emptyList();
        }
        List<CategVO> rst=new ArrayList<>();
        rows.forEach(i->rst.add(CopyWrapper.create(CategVO::new).endFrom(i).setCategId(i.getEventCategId())));
        return rst;
    }


}
