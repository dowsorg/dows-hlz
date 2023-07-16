package org.dows.hep.api.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumWebSocketType;

import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/7/11 11:08
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonWebSocketEventSource<T>{
    public CommonWebSocketEventSource(EnumWebSocketType socketType){
        this.socketType=socketType;
    }

    private EnumWebSocketType socketType;
    //实验id
    private String experimentInstanceId;
    //客户端accountId
    private Set<String> clientIds;
    //传输数据
    private T data;
}
