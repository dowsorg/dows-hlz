package org.dows.hep.api.notify.message;

import cn.hutool.json.JSONUtil;

/**
 * 实验消息
 */
public interface ExperimentMessage {

    default String getJsonString() {
        return JSONUtil.toJsonStr(this);
    }
}
