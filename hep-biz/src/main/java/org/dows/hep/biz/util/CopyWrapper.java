package org.dows.hep.biz.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.ArrayUtil;


import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/4/21 11:29
 */
public class CopyWrapper<T> {
    public static <T> CopyWrapper<T> create(T obj) {
        return new CopyWrapper(obj);
    }

    public static <T> CopyWrapper<T> create(Supplier<T> objFunc) {
        return new CopyWrapper(objFunc.get() );
    }

    private CopyWrapper(T obj) {
        this.coreObj = obj;
    }

    private CopyWrapper(Supplier<T> func) {
        this.coreObj = func.get();
    }

    volatile T coreObj;
    private final static CopyOptions s_copyOptions = CopyOptions.create()
            .setIgnoreCase(true)
            .setIgnoreError(true)
            .setIgnoreNullValue(false);


    public <S> CopyWrapper<T> from(S src,String... ignoreProperties) {
        return from(src, null,ignoreProperties);
    }

    public <S> CopyWrapper<T> from(S src, Consumer<T> func,String... ignoreProperties) {
        BeanUtil.copyProperties(src, coreObj, getOption(ignoreProperties));
        if (null != func) {
            func.accept(coreObj);
        }
        return this;
    }

    public T endFrom(Object... src) {
        if (ArrayUtil.isEmpty(src))
            return get();
        Arrays.stream(src).forEach(i -> BeanUtil.copyProperties(src, coreObj, s_copyOptions));
        return get();
    }

    public <S> T endFrom(S src,String... ignoreProperties) {
        return from(src,ignoreProperties).get();
    }

    public <S> T endFrom(S src, Consumer<T> func,String... ignoreProperties) {
        return from(src, func,ignoreProperties).get();
    }

    public T get() {
        return coreObj;
    }

    private CopyOptions getOption(String... ignoreProperties) {
        if (null == ignoreProperties || ignoreProperties.length == 0) {
            return s_copyOptions;
        }
        return CopyOptions.create()
                .setIgnoreCase(true)
                .setIgnoreError(true)
                .setIgnoreNullValue(false)
                .setIgnoreProperties(ignoreProperties);

    }
}
