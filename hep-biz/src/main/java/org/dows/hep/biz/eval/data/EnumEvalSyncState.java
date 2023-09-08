package org.dows.hep.biz.eval.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author : wuzl
 * @date : 2023/9/5 23:27
 */
@Getter
@RequiredArgsConstructor
public enum EnumEvalSyncState {
    NEW(0,"新增"),
    SYNCING(1,"同步中"),
    SYNCED2RD(2,"已同步redis"),
    SYNCED2DB(3,"已同步db")
    ;
    private final Integer code;
    private final String name;

    public static EnumEvalSyncState of(Integer code){
        return Arrays.stream( EnumEvalSyncState.values())
                .filter(i->i.getCode().equals(code))
                .findFirst()
                .orElse(NEW);
    }
}
