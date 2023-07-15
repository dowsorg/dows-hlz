package org.dows.hep.biz.cache;

import org.dows.hep.biz.vo.CategVO;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/7/14 20:16
 */
public class ExperimentCategCache extends BaseLoadingCache<String,CategCache> {


    private final Supplier<List<CategVO>> loadDataFunc;
    private static final int EXPIREInMinutes=60*24;
    public ExperimentCategCache(Supplier<List<CategVO>> loadDataFunc){
        super(1,4,EXPIREInMinutes*3,0);
        this.loadDataFunc=loadDataFunc;
    }

    @Override
    protected CategCache load(String key) {
        return new CategCache(EXPIREInMinutes,this.loadDataFunc);
    }
}
