package org.dows.hep.biz.spel.meta;

import java.util.Collection;

/**
 * @author : wuzl
 * @date : 2023/7/24 10:12
 */
public interface ISpelFlow {
    ISpelExecute withReasonId(String experimentId, String experimentPersonId, String reasonId, Integer source);

    ISpelExecuteBatch withReasonId(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source);

    ISpelExecute withReasonIdSilence(String experimentId, String experimentPersonId, String reasonId, Integer source);

    ISpelExecuteBatch withReasonIdSilence(String experimentId, String experimentPersonId, Collection<String> reasonIds, Integer source);

    ISpelExecute withExpressionId(String experimentId, String experimentPersonId, String expressionId, Integer source);

    ISpelExecuteBatch withExpressionId(String experimentId, String experimentPersonId, Collection<String> expressionIds, Integer source);

}
