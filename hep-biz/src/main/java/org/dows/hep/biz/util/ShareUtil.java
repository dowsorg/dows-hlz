package org.dows.hep.biz.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:23
 */
public class ShareUtil {
    public static class XObject {
        public static boolean isEmpty(Object obj){
            return ObjectUtils.isEmpty(obj);
        }

        public static boolean isAllEmpty(Object...objs) {
            return isEmpty(objs) || Arrays.stream(objs).allMatch(ObjectUtils::isEmpty);
        }
        public static boolean isAnyEmpty(Object...objs) {
            return isEmpty(objs) || Arrays.stream(objs).anyMatch(ObjectUtils::isEmpty);
        }
        public static boolean isAnyEmpty(Object obj, Supplier func){
            return isEmpty(obj)||isEmpty(func.get());
        }

        public static <T> T defaultIfNull(T object, T defaultValue) {
            return ObjectUtil.defaultIfNull(object,defaultValue);
        }

        public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
            return ObjectUtil.defaultIfNull(source,defaultValueSupplier);
        }




    }
    public static class XString {
        public static String defaultIfNull(String src, String dft) {
            return StringUtils.hasLength(src) ? src : dft;
        }
        public static String defaultIfNull(String src,String... dftValues) {
            if(StringUtils.hasLength(src)||null==dftValues|| dftValues.length==0) {
                return src;
            }
            for(String val : dftValues){
                if(StringUtils.hasLength(val)) {
                    return val;
                }
            }
            return src;
        }
        public static boolean hasLength(String src){
            return StringUtils.hasLength(src);
        }
        public static String eusureEndsWith(String src,String end){
            return src.endsWith(end)?src:String.format("%s%s",src,end);
        }
    }

    public static class XCollection {
        public static boolean isEmpty(Collection<?> collection){
            return CollectionUtils.isEmpty(collection);
        }
        public static boolean isEmpty(Map<?, ?> map){
            return CollectionUtils.isEmpty(map);
        }

        public static <T, R> List<R> map(Iterable<T> collection, boolean ignoreNull, Function<? super T, ? extends R> func){
            return CollUtil.map(collection,func,ignoreNull);
        }

    }

    public static class XArray {
        public static boolean isEmpty(Object array) {
            return ArrayUtil.isEmpty(array);
        }
    }


}
