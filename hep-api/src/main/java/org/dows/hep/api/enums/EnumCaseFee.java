package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumCaseFee implements StatusCode {
  CASE_FEE_UPDATE_EXCEPTION(40000, "更新案例费用失败"),
  ;
  private final Integer code;
  private final String descr;
}
