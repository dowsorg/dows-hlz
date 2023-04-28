package org.dows.hep.biz.funcs;

/**
 * @author : wuzl
 * @date : 2023/4/26 11:42
 */
@FunctionalInterface
public interface GetOrThrowFunc<T> {
    T orElseThrow(String msg);
}
