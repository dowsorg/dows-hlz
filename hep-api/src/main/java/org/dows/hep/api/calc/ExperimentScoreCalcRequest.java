package org.dows.hep.api.calc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.enums.EnumCalcCode;

import java.util.List;

/**
 * 实验分数计算请求
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ExperimentScoreCalcRequest {
    private String experimentInstanceId;
    private String experimentGroupId;
    private Integer period;
    private List<EnumCalcCode> enumCalcCodes;
}
