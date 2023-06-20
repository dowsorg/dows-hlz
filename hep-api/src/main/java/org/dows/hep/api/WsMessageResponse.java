package org.dows.hep.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.dows.hep.api.enums.EnumWebSocketType;

/**
 * @author fhb
 * @version 1.0
 * @description 事件通知结果
 * @date 2023/6/19 23:45
 **/
@Data
@AllArgsConstructor
@Builder
public class WsMessageResponse {
    private EnumWebSocketType type;
    private Object data;
}
