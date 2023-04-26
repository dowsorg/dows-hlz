package org.dows.hep.biz.cache;

import org.dows.hep.biz.vo.CategVO;

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
        return null;
    }


}
