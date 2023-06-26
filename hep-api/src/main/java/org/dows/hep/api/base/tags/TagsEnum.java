package org.dows.hep.api.base.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dows.framework.api.StatusCode;

/**
 * @author jx
 * @date 2023/6/26 14:42
 */
@Getter
@AllArgsConstructor
public enum TagsEnum implements StatusCode {
    TAGS_NAME_IS_REPEAT(42000, "标签名称重复"),
    ;
    private final Integer code;
    private final String descr;
}
