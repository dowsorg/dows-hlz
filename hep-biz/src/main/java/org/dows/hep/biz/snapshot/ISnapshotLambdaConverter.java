package org.dows.hep.biz.snapshot;

import com.baomidou.mybatisplus.core.toolkit.LambdaUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.apache.ibatis.reflection.property.PropertyNamer;
import org.dows.hep.biz.util.ShareUtil;

import java.util.*;

/**
 * @author : wuzl
 * @date : 2023/7/2 12:30
 */
public interface ISnapshotLambdaConverter<T> {

    List<SFunction<T,?>> getLambdaList();
    default Map<String, SFunction<T,?>> getLambdaMap(){
        Map<String, SFunction<T,?>>  rst=new HashMap<>();
        List<SFunction<T,?>> funcs=getLambdaList();
        if(ShareUtil.XObject.isEmpty(funcs)){
            return rst;
        }
        for(SFunction<T,?> item:funcs){
            rst.put(getLambdaKey(item), item);
        }
        return rst;
    }

    default <M> SFunction<T,?> convertFrom(SFunction<M,?> func){
        Map<String, SFunction<T,?>> map=getLambdaMap();
        return map.get(getLambdaKey(func));
    }

    default <M> List<SFunction<T,?>> convertFrom(List<SFunction<M,?>> funcs){
        List<SFunction<T,?>> rst=new ArrayList<>();
        if(ShareUtil.XObject.isEmpty(funcs)){
            return rst;
        }
        Map<String, SFunction<T,?>> map=getLambdaMap();
        SFunction<T,?> dst;
        for(SFunction<M,?> item:funcs){
            dst=map.get(getLambdaKey(item));
            if(null==dst){
                continue;
            }
            rst.add(dst);
        }
        return rst;
    }
    default <M> SFunction<T,?>[] convertFrom(SFunction<M,?>[] funcs){
        if(ShareUtil.XObject.isEmpty(funcs)){
            return new SFunction[0];
        }
        return convertFrom(Arrays.asList(funcs)).toArray(new SFunction[0]);
    }
    static <T> String getLambdaKey(SFunction<T,?> func){
        String key= PropertyNamer.methodToProperty(LambdaUtils.extract(func).getImplMethodName());
        return LambdaUtils.formatKey(key);
    }



}
