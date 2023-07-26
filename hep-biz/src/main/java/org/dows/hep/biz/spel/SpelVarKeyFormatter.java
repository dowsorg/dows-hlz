package org.dows.hep.biz.spel;

/**
 * @author : wuzl
 * @date : 2023/7/21 15:27
 */
public class SpelVarKeyFormatter {
    public static final String PREFIX="_";

    public static String getVariableKey(String src){
        if(src.startsWith(PREFIX)){
            return src;
        }
        return String.format("%s%s", PREFIX, src);
    }
}
