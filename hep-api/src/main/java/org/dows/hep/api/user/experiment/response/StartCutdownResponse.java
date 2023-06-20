package org.dows.hep.api.user.experiment.response;

import lombok.Data;
import org.dows.hep.api.enums.EnumWebSocketType;

/**
 * @author jx
 * @date 2023/6/20 11:39
 */
@Data
public class StartCutdownResponse {
    EnumWebSocketType enumWebSocketType;

    ExperimentPeriodsResonse experimentPeriodsResonse;
}
