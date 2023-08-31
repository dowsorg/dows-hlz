package org.dows.hep.biz.vo;

import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.event.sysevent.data.EventDealResult;
import org.dows.hep.biz.util.ShareUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/8/27 18:12
 */
@Data
@Accessors(chain = true)
public class PushWebScoketResult {

    private String type;
    private final Set<String> hitClients=new HashSet<>();

    private final Set<String> missClients=new HashSet<>();

    private final StringBuilder msg=new StringBuilder();

    private final String SPLITText=" ";

    public PushWebScoketResult append(String txt, Object...args) {
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
        sb.append("type:").append(type);
        sb.append(" hit:").append(String.join(",",hitClients));
        sb.append(" miss:").append(String.join(",",missClients));
        sb.append(" msg:").append(msg);
        sb.append('}');
        return sb.toString();
    }
}
