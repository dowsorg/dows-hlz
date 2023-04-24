package org.dows.hep.biz.enums;

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
  CATEGORY_IS_NOT_FIND(40001, "未找到该类别"),
  ;
  private final Integer code;
  private final String descr;
}
