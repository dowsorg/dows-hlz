package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoDescrRs;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoMonitorRs;
import org.dows.hep.api.base.indicator.request.CreateIndicatorViewBaseInfoSingleRs;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicatorViewBaseInfoResponseRs implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;
    @Schema(title = "分布式ID")
    private String indicatorViewBaseInfoId;
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "指标功能ID")
    private String indicatorFuncId;
    private List<IndicatorViewBaseInfoDescrResponseRs> indicatorViewBaseInfoDescrResponseRsList;
    private List<IndicatorViewBaseInfoMonitorResponseRs> indicatorViewBaseInfoMonitorResponseRsList;
    private List<IndicatorViewBaseInfoSingleResponseRs> indicatorViewBaseInfoSingleResponseRsList;
}