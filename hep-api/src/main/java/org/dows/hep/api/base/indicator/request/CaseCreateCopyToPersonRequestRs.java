package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@Schema(name = "CaseCreateCopyToPersonRequestRs对象", title = "创建复制人物指标去人物")
public class CaseCreateCopyToPersonRequestRs {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "案例人物")
  private String casePersonId;
}
