package org.dows.hep.biz.util;

import org.dows.hep.biz.funcs.ThrowMessageFunc;

/**
 * @author : wuzl
 * @date : 2023/4/23 10:10
 */
public class AssertUtil {
    public static ThrowMessageFunc trueThenThrow(boolean flag){
        return msg->{
            if(flag){
                throw new RuntimeException(msg);
            }
        };
    }
    public static ThrowMessageFunc falseThenThrow(boolean flag){
        return msg->{
            if(!flag){
                throw new RuntimeException(msg);
            }
        };
    }

}
