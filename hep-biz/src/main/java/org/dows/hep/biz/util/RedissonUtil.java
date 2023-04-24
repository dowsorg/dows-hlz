package org.dows.hep.biz.util;


import org.dows.hep.biz.enums.EnumRedissonLock;

/**
 * @author runsix
 */
public class RedissonUtil {
    public static String getLockName(String appId, EnumRedissonLock enumRedissonLock, String fieldName, String fieldValue) {
        return String.format("lock:app:%s:%s:%s:%s", appId, enumRedissonLock.getSituation(), fieldName, fieldValue);
    }
}
