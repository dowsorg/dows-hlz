package org.dows.hep.biz.eval.codec;

import org.redisson.api.RMap;

import java.util.Map;

/**
 * @author : wuzl
 * @date : 2023/9/6 15:16
 */
public interface IRDMapCodec<T> {
    T fromRDMap(RMap<String,String> map);

    Map<String,String> toRDMap(T obj);
}
