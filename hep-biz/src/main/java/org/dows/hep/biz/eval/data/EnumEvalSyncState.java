package org.dows.hep.biz.eval.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:27
 */
@Getter
@RequiredArgsConstructor
public enum EnumEvalSyncState {
    NEW(0,"新增"),
    SYNCING(1,"同步中"),
    SYNCED(2,"已同步")
    ;
    private final Integer code;
    private final String name;
}
