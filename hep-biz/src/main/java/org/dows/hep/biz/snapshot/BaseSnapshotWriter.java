package org.dows.hep.biz.snapshot;

import cn.hutool.crypto.SecureUtil;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.biz.dao.ExperimentSnapshotRefDao;
import org.dows.hep.biz.util.JacksonUtil;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSnapshotRefEntity;
import org.dows.sequence.api.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author : wuzl
 * @date : 2023/6/27 15:18
 */
@Slf4j
public abstract class BaseSnapshotWriter<T> implements ISnapshotDbWriter<T> {


    public abstract EnumSnapshotType getSnapshotType();


    public abstract T readSource(SnapshotRequest req);

    @Autowired
    protected IdGenerator idGenerator;
    @Autowired
    protected ExperimentSnapshotRefDao experimentSnapshotRefDao;

    protected abstract boolean saveSnapshotData(SnapshotRequest req,T data);


    @Override
    public boolean write(SnapshotRequest req,T data) {
        if(ShareUtil.XObject.isEmpty(data)){
            return true;
        }
        try {
            final EnumSnapshotType snapshotType=getSnapshotType();
            final String appId=req.getAppId();
            final String experimentId=req.getExperimentInstanceId();
            String md5 = SecureUtil.md5(JacksonUtil.toJson(data, true));
            String refExperimentId=SnapshotRefCache.Instance().getRefExperimentId(appId ,snapshotType,experimentId);
            if(ShareUtil.XObject.notEmpty(refExperimentId)){
                return true;
            }
            refExperimentId= SnapshotRefCache.Instance().getExperimentIdByMd5(appId ,snapshotType, md5);
            if(experimentId.equals(refExperimentId)){
                return true;
            }
            ExperimentSnapshotRefEntity rowRef=ExperimentSnapshotRefEntity.builder()
                    .experimentSnapshotRefId(idGenerator.nextIdStr())
                    .appId(req.getAppId())
                    .experimentInstanceId(req.getExperimentInstanceId())
                    .snapshotType(snapshotType.getCode().toLowerCase())
                    .srcTableName(snapshotType.getSrcTableName())
                    .md5(md5)
                    .refExperimentInstanceId(refExperimentId)
                    .build();
            boolean rst;
            if(ShareUtil.XObject.isEmpty(refExperimentId)){
                rst= experimentSnapshotRefDao.tranSave(rowRef,false, ()->saveSnapshotData(req,data));
            }else{
                rst=experimentSnapshotRefDao.tranSave(rowRef);
            }
            if(!rst){
                logError("write","快照写入失败. %s",req );
            }
            SnapshotRefCache.Instance().removeExperimentIdByMd5(req.getAppId(),snapshotType,md5);
            SnapshotRefCache.Instance().removeRefExperimentId(req.getAppId(),snapshotType,experimentId);
            return rst;
        }catch(Exception ex){
            logError(ex,"write","快照写入异常. %s",req );
            return false;
        }

    }

    protected void logError(String func, String msg,Object... args){
        logError(null, func,msg,args);
    }
    protected void logError(Throwable ex, String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] %s", this.getClass().getName(), func, LocalDateTime.now(),this.hashCode(),
                String.format(Optional.ofNullable(msg).orElse(""), args));
        log.error(str,ex);
    }
    protected void logInfo(String func, String msg,Object... args){
        String str=String.format("%s.%s@%s[%s] type:%s %s",this.getClass().getName(),func,LocalDateTime.now(),this.hashCode(),
                getSnapshotType().getName(),String.format(Optional.ofNullable(msg).orElse(""), args));
        log.info(str);
    }
}
