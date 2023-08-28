package org.dows.hep.biz.event.sysevent.data;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.util.ShareUtil;

import java.time.LocalDateTime;

/**
 * @author : wuzl
 * @date : 2023/8/24 19:21
 */
@Data
@Accessors(chain = true)
public class EventDealResult {
    private boolean succ;
    private final StringBuilder msg=new StringBuilder();
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private EnumSysEventDealType nextDeal;
    private LocalDateTime nextTime;
    private Integer retryTimes;

    private SysEventRunStat flowStat;

    private final String SPLITText=" ";

    public EventDealResult append(String txt,Object...args) {
        if(msg.length()>0) {
            msg.append(SPLITText);
        }
        if (ShareUtil.XObject.isEmpty(args)) {
            msg.append(txt);
        }else {
            msg.append(String.format(txt, args));
        }
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("succ:").append(succ);
        sb.append(" start:").append(startTime);
        sb.append(" end:").append(endTime);
        sb.append(" nextDeal:").append(nextDeal);
        sb.append(" nextTime:").append(nextTime);
        sb.append(" retry:").append(retryTimes);
        sb.append(" msg:").append(msg);
        sb.append(" flow:").append(flowStat);
        sb.append('}');
        return sb.toString();
    }
}
