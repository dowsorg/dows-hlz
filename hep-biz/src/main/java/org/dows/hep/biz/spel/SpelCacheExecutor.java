package org.dows.hep.biz.spel;

import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.event.data.ExperimentCacheKey;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author : wuzl
 * @date : 2023/9/14 12:18
 */
@Component
@Slf4j
public class SpelCacheExecutor {

    private static volatile SpelCacheExecutor s_instance;

    public static SpelCacheExecutor Instance(){
        return s_instance;
    }

    private SpelCacheExecutor(){
        s_instance=this;
    }
    private final static int CONCURRENTNum=3;

    public void start(List<String> ids){
        if(ShareUtil.XObject.isEmpty(ids)){
            return;
        }
        List<List<String>> exptIds= ShareUtil.XCollection.split(ids,CONCURRENTNum);
        exptIds.forEach(item->new SpelCacheLoadThread(new HashSet<>(item)).start());
    }

    public class SpelCacheLoadThread extends Thread {
        public SpelCacheLoadThread(Set<String> ids){
            this.experimentIds=ids;
        }
        private final Set<String> experimentIds;
        private final static String APPId="3";

        @Override
        public void run() {
            StringBuilder sb=new StringBuilder();
            long ts=logCostTime(sb,"SPELTRACE--load--");
            for(String experimentId:experimentIds){
                ExperimentCacheKey key=ExperimentCacheKey.create(APPId,experimentId);
                try{
                    ExperimentSpelCache.Instance().loadingCache().get(key);
                }catch (Exception ex){
                    ts=logCostTime(sb,String.format("loaderror %s %s", experimentId,ex.getMessage()),ts);
                    log.error(String.format("SPELTRACE--load--error exptKey[%s]",key), ex);
                }
                ts=logCostTime(sb,String.format("loaded %s", experimentId),ts);
            }
            log.info(sb.toString());
            log.error(sb.toString());

        }

        private long logCostTime(StringBuilder sb,String start){
            sb.append(start);
            return System.currentTimeMillis();
        }

        long logCostTime(StringBuilder sb,String func,long ts){
            long newTs=System.currentTimeMillis();
            sb.append(" ").append(func).append(":").append((newTs-ts));
            return newTs;
        }
    }
}
