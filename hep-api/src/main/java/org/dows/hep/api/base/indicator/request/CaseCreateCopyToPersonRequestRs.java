package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "CaseCreateCopyToPersonRequestRs对象", title = "创建复制人物指标去人物")
public class CaseCreateCopyToPersonRequestRs {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标主体id，数据库人物管理的时候，这个是uim的account_id")
  private String principalId;
}
