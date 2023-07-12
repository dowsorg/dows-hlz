package org.dows.hep.event.handler;

import org.dows.hep.api.user.experiment.response.OrgNoticeResponse;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 突发事件已触发
 *
 * @author : wuzl
 * @date : 2023/7/11 9:56
 */
@Component
public class ExptEventTriggeredHandler extends CommonWebSocketEventHandler<List<OrgNoticeResponse>> {

}
