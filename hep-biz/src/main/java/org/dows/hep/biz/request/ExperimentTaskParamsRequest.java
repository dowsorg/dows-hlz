package org.dows.hep.biz.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/7/28 11:37
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentTaskParamsRequest {
    private String experimentInstanceId;
    private String experimentGroupId;
    private Integer period;
    private String noticeParams;
    private Integer noticeType;
}
