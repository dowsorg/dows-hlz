package org.dows.hep.biz.eval.data;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.event.data.ExperimentTimePoint;

/**
 * @author : wuzl
 * @date : 2023/9/7 15:49
 */
@Data
@Accessors(chain = true)
public class EvalPersonSyncRequest {

    private ExperimentTimePoint timePoint;

    private EnumEvalFuncType funcType;

    public boolean isPeriodInit(){
        return funcType==EnumEvalFuncType.PERIODEnd;
    }
}
