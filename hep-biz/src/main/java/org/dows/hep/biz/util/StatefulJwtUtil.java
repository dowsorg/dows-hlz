package org.dows.hep.biz.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author jx
 * @date 2023/9/6 18:33
 */
public class StatefulJwtUtil {
    public static final Map<String, String> TOKENS = new HashMap<String, String>();

    public static String putToken(String token, String accountId) {
        // 保存token
        TOKENS.put(accountId, token);
        return token;
    }
}
