package org.dows.hep.biz.eval.data;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;
import org.dows.hep.entity.ExperimentEvalLogEntity;
import org.dows.hep.entity.ExperimentIndicatorLogEntity;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/6 23:53
 */
@Data
@Accessors(chain = true)
public class EvalPersonToSavePack {

    private EvalPersonOnceData.Header header;
    private ExperimentEvalLogEntity logEval;

    private ExperimentEvalLogContentEntity logEvalContent;

    private List<ExperimentIndicatorLogEntity> logIndicators;

}
