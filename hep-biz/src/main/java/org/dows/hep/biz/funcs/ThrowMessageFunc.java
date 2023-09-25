package org.dows.hep.biz.funcs;

/**
 * @author : wuzl
 * @date : 2023/4/23 10:31
 */
@FunctionalInterface
public interface ThrowMessageFunc {

    void throwMessage(String msg);

    default void throwMessage(String msg,Object...args){
        throwMessage(String.format(msg, args));
    }

}
