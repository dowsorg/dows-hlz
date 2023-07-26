package org.dows.hep.biz.spel.meta;

import java.util.function.Consumer;

/**
 * @author : wuzl
 * @date : 2023/7/21 11:47
 */
public interface ISpelExecute extends ISpelCheck, ISpelEval {

    SpelInput getInput();
    default ISpelExecute preExecute(Consumer<SpelInput> func){
        func.accept(getInput());
        return this;
    }
}
