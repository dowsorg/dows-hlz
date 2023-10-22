package org.dows.hep.biz.spel.meta;

import java.util.Collection;
import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/21 10:49
 */
public interface ISpelLoad {

    SpelInput withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source,Integer... sources);

    List<SpelInput> withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source,Integer... sources);

    SpelInput withExpressionId(String experimentId, String experimentPersonId, String expressionId, Integer source);

    List<SpelInput> withExpressionId(String experimentId, String experimentPersonId, Collection<String> expressionIds, Integer source);

}
