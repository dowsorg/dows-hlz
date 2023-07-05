package org.dows.hep.biz.snapshot;

import com.baomidou.mybatisplus.core.toolkit.support.SFunction;

import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/7/3 11:57
 */
public abstract class BaseSnapshotLambdaConverter<T> implements ISnapshotLambdaConverter<T> {

    protected BaseSnapshotLambdaConverter(){
        getLambdaMap();
    }
    protected volatile Map<String, SFunction<T,?>> mapFunc;

    @Override
    public Map<String, SFunction<T, ?>> getLambdaMap() {
        if(null!=mapFunc){
            return mapFunc;
        }
        return mapFunc=ISnapshotLambdaConverter.super.getLambdaMap();
    }
}
