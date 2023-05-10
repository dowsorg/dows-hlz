package org.dows.hep.biz.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:23
 */
public class ShareUtil {
    public static class XObject {

        public static boolean notEmpty(Number obj,boolean zeroAsEmpty){
            return !isEmpty(obj,zeroAsEmpty);
        }
        public static boolean notEmpty(Object obj){
            return !isEmpty(obj);
        }
        public static boolean isEmpty(Number obj,boolean zeroAsEmpty){
            return ObjectUtils.isEmpty(obj)||zeroAsEmpty && obj.equals(0);
        }
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

        public static boolean nullSafeEquals(Object o1, Object o2){
            return ObjectUtils.nullSafeEquals(o1,o2);
        }




    }
    public static class XString {
        public static String defaultIfEmpty(String src, String dft) {
            return StringUtils.hasLength(src) ? src : dft;
        }
        public static String defaultIfEmpty(String src, String... dftValues) {
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

        public static boolean nullSafeEquals(String o1, String o2,boolean ignoreCase) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }
            return ignoreCase ? o1.equalsIgnoreCase(o2) : o1.equals(o2);
        }

        public static boolean hasLength(String src){
            return StringUtils.hasLength(src);
        }
        public static String eusureEndsWith(String src,String end){
            return src.endsWith(end)?src:String.format("%s%s",src,end);
        }
    }

    public static class XCollection {

        public static boolean notEmpty(Collection<?> collection) {
            return !isEmpty(collection);
        }

        public static boolean isEmpty(Collection<?> collection) {
            return CollectionUtils.isEmpty(collection);
        }

        public static boolean notEmpty(Map<?, ?> map) {
            return !isEmpty(map);
        }

        public static boolean isEmpty(Map<?, ?> map) {
            return CollectionUtils.isEmpty(map);
        }

        public static <T, R> List<R> map(Iterable<T> collection, boolean ignoreNull, Function<? super T, ? extends R> func) {
            return CollUtil.map(collection, func, ignoreNull);
        }

        public static <T, K, U> Map<K, U> toMap(List<T> src, Function<? super T, ? extends K> keyMapper,
                                                Function<? super T, ? extends U> valueMapper) {
            if (isEmpty(src)) {
                return new HashMap<>(0);
            }
            return src.stream().collect(Collectors.toMap(keyMapper, valueMapper, (c, n) -> c));
        }

        public static <T, K, U, M extends Map<K, U>> M toMap(List<T> src, Function<? super T, ? extends K> keyMapper,
                                                             Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction, Supplier<M> mapFactory) {
            return src.stream().collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction, mapFactory));
        }

    }

    public static class XArray {
        public static boolean isEmpty(Object array) {
            return ArrayUtil.isEmpty(array);
        }
        public static boolean notEmpty(Object array) {
            return !isEmpty(array);
        }

        public static boolean contains(T[] array, T value) {
            return ArrayUtil.contains(array, value);
        }

    }


}
