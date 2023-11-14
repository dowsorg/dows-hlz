package org.dows.hep.biz.util;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:23
 */
public class ShareUtil {
    public static class XObject {

        public static boolean notEmpty(Object obj) {
            return !isEmpty(obj);
        }

        public static boolean notEmpty(Number obj, boolean zeroAsEmpty) {
            return !isEmpty(obj, zeroAsEmpty);
        }

        public static boolean isEmpty(Object obj) {
            return ObjectUtils.isEmpty(obj);
        }

        public static boolean isEmpty(Number obj, boolean zeroAsEmpty) {
            return ObjectUtils.isEmpty(obj) || zeroAsEmpty && obj.equals(0);
        }


        public static boolean allEmpty(Object... objs) {
            if(isEmpty(objs)) return true;
            for(Object item:objs){
                if(notEmpty(item)) return false;
            }
            return true;
        }
        public static boolean allEmpty(Object obj, Supplier func) {
            return isEmpty(obj) && isEmpty(func.get());
        }
        public static boolean noneEmpty(Object... objs) {
            if(isEmpty(objs)) return false;
            for(Object item:objs){
                if(isEmpty(item)) return false;
            }
            return true;
        }
        public static boolean noneEmpty(Object obj, Supplier func) {
            return notEmpty(obj) && notEmpty(func.get());
        }

        public static boolean anyEmpty(Object... objs) {
            if(isEmpty(objs)) return true;
            for(Object item:objs){
                if(isEmpty(item)) return true;
            }
            return false;
        }

        public static boolean anyEmpty(Object obj, Supplier func) {
            return isEmpty(obj) || isEmpty(func.get());
        }

        public static boolean anyNotEmpty(Object... objs) {
            if(isEmpty(objs)) return false;
            for(Object item:objs){
                if(notEmpty(item)) return true;
            }
            return false;
        }


        public static boolean isNumber(Object obj){
            if(isEmpty(obj)){
                return false;
            }
            if(obj instanceof Number){
                return true;
            }
            return isNumber(obj.toString());
        }
        public static boolean notNumber(Object obj){
            return !isNumber(obj);
        }
        public static boolean isNumber(String str){
            if(isEmpty(str)){
                return false;
            }
            return NumberUtils.isCreatable(str.trim());
        }
        public static boolean notNumber(String str){
            return !isNumber(str);
        }
        public static <T> T defaultIfNull(T object, T defaultValue) {
            return ObjectUtil.defaultIfNull(object, defaultValue);
        }

        public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
            return ObjectUtil.defaultIfNull(source, defaultValueSupplier);
        }

        public static boolean nullSafeEquals(Object o1, Object o2) {
            return ObjectUtils.nullSafeEquals(o1, o2);
        }
        public static boolean nullSafeNotEquals(Object o1, Object o2) {
            return !ObjectUtils.nullSafeEquals(o1, o2);
        }

        public static String trim(String src){
            return trim(src,"");
        }
        public static String trim(String src,String dft){
            return null==src?dft:src.trim();
        }


    }

    public static class XString {
        public static String defaultIfEmpty(String src, String dft) {
            return StringUtils.hasLength(src) ? src : dft;
        }
        public static String defaultIfEmpty(String src, Supplier<String> dftSupplier) {
            return StringUtils.hasLength(src) ? src : dftSupplier.get();
        }

        public static String defaultIfEmpty(String src, String... dftValues) {
            if (StringUtils.hasLength(src) || null == dftValues || dftValues.length == 0) {
                return src;
            }
            for (String val : dftValues) {
                if (StringUtils.hasLength(val)) {
                    return val;
                }
            }
            return src;
        }

        public static boolean nullSafeEquals(String o1, String o2, boolean ignoreCase) {
            if (o1 == o2) {
                return true;
            }
            if (o1 == null || o2 == null) {
                return false;
            }
            return ignoreCase ? o1.equalsIgnoreCase(o2) : o1.equals(o2);
        }

        public static boolean hasLength(String src) {
            return StringUtils.hasLength(src);
        }

        public static String eusureEndsWith(String src, String end) {
            return src.endsWith(end) ? src : String.format("%s%s", src, end);
        }

        public static String trimStart(String src, String replace) {
            return src.startsWith(replace)?src.substring(replace.length()):src;
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

        public static <T> List<List<T>> split(List<T> src,int splitNum){
            if(ShareUtil.XObject.isEmpty(src)){
                return Collections.emptyList();
            }
            int size=(src.size()+splitNum-1)/splitNum;
            return Lists.partition(src, size).stream().map(ArrayList::new).collect(Collectors.toList());
        }

        public static <T> List<List<T>> splitByBatchSize(List<T> src,int batchSize){
            if(ShareUtil.XObject.isEmpty(src)){
                return Collections.emptyList();
            }
            return Lists.partition(src, batchSize).stream().map(ArrayList::new).collect(Collectors.toList());
        }

        public static <T, R> List<R> map(Iterable<T> collection,  Function<? super T, ? extends R> func) {
            return CollUtil.map(collection, func, true);
        }

        public static <T, R> List<R> map(Iterable<T> collection, boolean ignoreNull, Function<? super T, ? extends R> func) {
            return CollUtil.map(collection, func, ignoreNull);
        }

        public static <T,R> Set<R> toSet(Collection<T> src,Function<? super T, ? extends R> mapper ){
            if(isEmpty(src)){
                return new HashSet<>(0);
            }
            return src.stream().map(mapper).collect(Collectors.toSet());
        }
        public static <T, K> Map<K, T> toMap(Collection<T> src, Function<? super T, ? extends K> keyMapper) {
            return toMap(src, keyMapper, Function.identity(), false);
        }
        public static <T, K, U> Map<K, U> toMap(Collection<T> src, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
           return toMap(src, keyMapper, valueMapper, false);
        }
        public static <T, K, U> Map<K, U> toMap(Collection<T> src, Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper,boolean preferNew) {
            if (isEmpty(src)) {
                return new HashMap<>(0);
            }
            return src.stream().collect(Collectors.toMap(keyMapper, valueMapper, (c, n) -> preferNew ? n : c));
        }
        public static <T, K, U, M extends Map<K, U>> M toMap(Collection<T> src, Supplier<M> mapFactory, Function<? super T, ? extends K> keyMapper,
                                                             Function<? super T, ? extends U> valueMapper, boolean preferNew) {
            return src.stream().collect(Collectors.toMap(keyMapper, valueMapper, (c, n) -> preferNew ? n : c, mapFactory));
        }
        public static <T, K, U, M extends Map<K, U>> M toMap(Collection<T> src, Supplier<M> mapFactory, Function<? super T, ? extends K> keyMapper,
                                                             Function<? super T, ? extends U> valueMapper, BinaryOperator<U> mergeFunction) {
            return src.stream().collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction, mapFactory));
        }


        public static <T, K> Map<K,List<T>> groupBy(Collection<T> src, Function<? super T, ? extends K> keyMapper) {
            return src.stream().collect(Collectors.groupingBy(keyMapper, HashMap::new, Collectors.toList()));
        }
        public static <T,U, K> Map<K,List<U>> groupBy(Collection<T> src, Function<? super T, ? extends U> mapper, Function<? super U, ? extends K> keyMapper) {
            return src.stream().map(mapper).collect(Collectors.groupingBy(keyMapper, HashMap::new, Collectors.toList()));
        }
        public static <T, K, U,D,A, M extends Map<K, D>> M groupBy(Collection<T> src, Supplier<M> mapFactory, Function<? super T, ? extends U> mapper, Function<? super U, ? extends K> keyMapper, Collector<? super U, A, D> downstream){
            return src.stream().map(mapper).collect(Collectors.groupingBy(keyMapper,mapFactory,downstream));
        }



    }

    public static class XArray {
        public static boolean isEmpty(Object array) {
            return ArrayUtil.isEmpty(array);
        }

        public static boolean notEmpty(Object array) {
            return !isEmpty(array);
        }

        public static <T> boolean contains(T[] array, T value) {
            return ArrayUtil.contains(array, value);
        }

    }

    public static class XDate {
        static final ZoneId DFTTimeZone = ZoneId.systemDefault();

        public static LocalDateTime localDT4UnixTS(long ts, boolean secondFlag) {
            return LocalDateTime.ofInstant(secondFlag ? Instant.ofEpochSecond(ts) : Instant.ofEpochMilli(ts), DFTTimeZone);
        }
        public static long localDT2UnixTS(LocalDateTime dt, boolean secondFlag) {
            return secondFlag ? dt.atZone(DFTTimeZone).toEpochSecond() : dt.atZone(DFTTimeZone).toInstant().toEpochMilli();
        }
        public static LocalDateTime localDT4Date(Date dt) {
            if(null==dt){
                return null;
            }
            return dt.toInstant().atZone(DFTTimeZone).toLocalDateTime();
        }
        public static Date localDT2Date(LocalDateTime dt) {
            if(null==dt){
                return null;
            }
            return Date.from(dt.atZone(DFTTimeZone).toInstant());
        }


    }
    public static class XRandom {
        public static boolean randomBoolean(){
            return randomInteger(0, 2)==0;
        }

        public static BigDecimal randomBigDecimal(BigDecimal min, BigDecimal max, int scale){
            if(min.compareTo(max)>0){
                BigDecimal v=max;
                min=max;
                max=v;
            }
            return BigDecimalUtil.valueOf(randomDouble(min.doubleValue(), max.doubleValue()))
                    .setScale(scale, RoundingMode.DOWN);
        }
        public static int randomInteger(int min, int max){
            return ThreadLocalRandom.current().nextInt(min,max);
        }

        private static double randomDouble(double min, double max){
            return ThreadLocalRandom.current().nextDouble(min,max);
        }

    }




}
