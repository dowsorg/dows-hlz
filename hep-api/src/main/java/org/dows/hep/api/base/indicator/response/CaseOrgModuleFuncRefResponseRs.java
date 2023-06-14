package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseOrgModuleFuncRefResponseRs implements Serializable {
  @Schema(title = "分布式ID")
  private String caseOrgModuleFuncRefId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String caseOrgModuleId;

  @Schema(title = "功能类别")
  private IndicatorFuncResponse indicatorFuncResponse;

  @JsonIgnore
  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @Schema(title = "时间戳")
  private Date dt;
}
