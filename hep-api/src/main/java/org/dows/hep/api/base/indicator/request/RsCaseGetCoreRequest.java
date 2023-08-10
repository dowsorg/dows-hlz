package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "RsCaseGetCoreRequest对象", title = "根据accountId列表获取核心指标串")
public class RsCaseGetCoreRequest implements Serializable {
  @Schema(title = "accountId列表")
  private List<String> accountIdList;
}
