package org.dows.hep.api.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author runsix
 */
@AllArgsConstructor
@Getter
public enum EnumMaterials implements StatusCode {
  CATEGORY_NAME_IS_EXIST(40000, "类别名称已存在"),
  ;
  private final Integer code;
  private final String descr;
}
