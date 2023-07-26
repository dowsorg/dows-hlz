package org.dows.hep.biz.spel.meta;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author : wuzl
 * @date : 2023/7/21 11:48
 */
public interface ISpelExecuteBatch extends ISpelCheckBatch, ISpelEvalBatch {
    List<SpelInput> getInput();
    default ISpelExecuteBatch prepare(Consumer<List<SpelInput>> func){
        func.accept(getInput());
        return this;
    }
}
