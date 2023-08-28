package org.dows.hep.biz.event.sysevent.data;

import org.dows.hep.biz.event.data.ExperimentTimePoint;
import org.dows.hep.biz.util.ShareUtil;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author : wuzl
 * @date : 2023/8/24 10:04
 */

public class SysEventRunStat {

    public final AtomicInteger doneCounter=new AtomicInteger();

    public final AtomicInteger failCounter=new AtomicInteger();

    public final AtomicInteger todoCounter=new AtomicInteger();

    private final StringBuilder msg=new StringBuilder();

    //当前时间点
    public final AtomicReference<ExperimentTimePoint> curTimePoint=new AtomicReference<>();

    //下次触发时间
    public final AtomicReference<LocalDateTime> nextTriggerIime=new AtomicReference<>();


    private final String SPLITText=",";

    public void clear(){
        msg.setLength(0);
    }

    public SysEventRunStat append(String txt,Object...args) {
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
        sb.append("done:").append(doneCounter);
        sb.append(" fail:").append(failCounter);
        sb.append(" todo:").append(todoCounter);
        sb.append(" cur:").append(curTimePoint);
        sb.append(" next:").append(nextTriggerIime);
        sb.append(" msg:").append(msg);
        sb.append('}');
        return sb.toString();
    }
}
