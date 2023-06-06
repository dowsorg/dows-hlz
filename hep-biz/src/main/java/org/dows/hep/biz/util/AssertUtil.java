package org.dows.hep.biz.util;

import org.dows.framework.api.exceptions.BaseException;
import org.dows.hep.biz.funcs.GetOrThrowFunc;
import org.dows.hep.biz.funcs.ThrowMessageFunc;

import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/4/23 10:10
 */
public class AssertUtil {
    public static ThrowMessageFunc trueThenThrow(boolean flag){
        return msg->{
            if(flag){
                throw createException(msg);
            }
        };
    }
    public static ThrowMessageFunc falseThenThrow(boolean flag){
        return msg->{
            if(!flag){
                throw createException(msg);
            }
        };
    }
    public static <T>  GetOrThrowFunc<T> getNotNull(T obj){
        return msg->{
          if(null==obj){
              throw createException(msg);
          }
          return obj;
        };
    }

    public static <T>  GetOrThrowFunc<T> getNotNull(Optional<T> obj) {
        return msg -> obj.orElseThrow(() -> createException(msg));
    }

    public static void justThrow(String msg,Throwable throwable){
        throw createException(msg,throwable);
    }
    static RuntimeException createException(String msg){
        return new BaseException(msg);
    }

    static RuntimeException createException(String msg,Throwable throwable){
        return new BaseException(msg,throwable);
    }

}
