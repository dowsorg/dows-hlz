package org.dows.hep.biz.snapshot;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : wuzl
 * @date : 2023/6/27 14:19
 */
@Component
@Slf4j
public class SnapshotManager implements ISnapshotWriter, ApplicationContextAware {
    private static volatile SnapshotManager s_instance;
    public static SnapshotManager Instance(){
        return s_instance;
    }

    private static volatile List<ISnapshotDbWriter> s_writers;
    public SnapshotManager(){
        s_instance=this;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            Map<String,ISnapshotDbWriter> map= applicationContext.getBeansOfType(ISnapshotDbWriter.class);
            if(ShareUtil.XObject.isEmpty(map)){
                log.error("SnapshotManager.setApplicationContext emptyWriters");
                return;
            }
            List<ISnapshotDbWriter> writers=map.values().stream()
                    .sorted(Comparator.comparingInt(i->i.getSnapshotType().getWriteOrder()))
                    .collect(Collectors.toList());
            s_writers=writers;
        }catch (Exception ex){
            log.error("SnapshotManager.setApplicationContext",ex);
        }
    }


    @Override
    public boolean write(SnapshotRequest req) {
       return write(req, false);
    }

    public boolean write(SnapshotRequest req,boolean incManual) {
        boolean rst=true;
        List<ISnapshotDbWriter> writers=s_writers;
        for(ISnapshotDbWriter item:writers){
            if(!incManual&&item.manulFlag()){
                continue;
            }
            rst&=item.write(req);
        }
        return rst;
    }
    public boolean write(SnapshotRequest req,EnumSnapshotType snapshotType,EnumSnapshotType...snapshotTypes){
        boolean rst=true;
        List<ISnapshotDbWriter> writers=s_writers;
        EnumSnapshotType itemSnapType;
        for(ISnapshotDbWriter item:writers){
            itemSnapType=item.getSnapshotType();
            if(snapshotType==itemSnapType
                    ||ShareUtil.XObject.notEmpty(snapshotTypes) &&ShareUtil.XArray.contains(snapshotTypes,itemSnapType))
            {
                rst&=item.write(req);
            }
        }
        return rst;
    }

    @DSTransactional
    public boolean writeWithTran(SnapshotRequest req){
        return write(req);
    }
    @DSTransactional
    public boolean writeWithTran(SnapshotRequest req,boolean incManual){
        return write(req, incManual);
    }
    @DSTransactional
    public boolean writeWithTran(SnapshotRequest req,EnumSnapshotType snapshotType,EnumSnapshotType...snapshotTypes) {
        return write(req,snapshotType,snapshotTypes);
    }


}
