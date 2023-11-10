package org.dows.hep.biz.snapshot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.biz.cache.BaseLoadingCache;
import org.dows.hep.biz.dao.ExperimentSnapshotRefDao;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.ExperimentSnapshotRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/6/28 16:53
 */
@Component
public class SnapshotRefCache extends BaseLoadingCache<SnapshotRefCache.SnapshotCacheKey, String> {

    private static volatile SnapshotRefCache s_instance;

    public static SnapshotRefCache Instance(){
        return s_instance;
    }
    protected final static int CACHEInitCapacity=50;
    protected final static int CACHEMaxSize=500;
    protected final static int CACHEExpireSeconds=60*60;

    private SnapshotRefCache(){
        super(CACHEInitCapacity,CACHEMaxSize,CACHEExpireSeconds,0);
        s_instance=this;
    }

    @Autowired
    private ExperimentSnapshotRefDao experimentSnapshotRefDao;


    public void removeExperimentIdByMd5(String appId,EnumSnapshotType snapshotType,String md5){
        this.loadingCache().invalidate(getMd5Key(appId,snapshotType,md5));
    }

    public String getExperimentIdByMd5(String appId,EnumSnapshotType snapshotType,String md5){

        return this.loadingCache().get(getMd5Key(appId,snapshotType,md5));
    }
    public void removeRefExperimentId(String appId,EnumSnapshotType snapshotType,String experimentId) {
        this.loadingCache().invalidate(getExperimentKey(appId,snapshotType,experimentId));
    }
    public String getRefExperimentId(String appId,EnumSnapshotType snapshotType,String experimentId){
        return this.loadingCache().get(getExperimentKey(appId,snapshotType,experimentId));
    }
    SnapshotCacheKey getExperimentKey(String appId,EnumSnapshotType snapshotType,String experimentId){
        return SnapshotCacheKey.builder()
                .appId(appId)
                .snapshotType(snapshotType)
                .experimentInstanceId(experimentId)
                .md5(null)
                .build();
    }
    SnapshotCacheKey getMd5Key(String appId,EnumSnapshotType snapshotType,String md5){
        return SnapshotCacheKey.builder()
                .appId(appId)
                .snapshotType(snapshotType)
                .experimentInstanceId(null)
                .md5(md5)
                .build();
    }

    @Override
    protected String load(SnapshotCacheKey key) {
        if(ShareUtil.XObject.isEmpty(key)){
            return null;
        }
        if(ShareUtil.XObject.notEmpty(key.getMd5())){
            return experimentSnapshotRefDao.getByMd5(key.getAppId(), key.getSnapshotType().getCode().toLowerCase(), key.getMd5(),ExperimentSnapshotRefEntity::getExperimentInstanceId)
                    .map(ExperimentSnapshotRefEntity::getExperimentInstanceId)
                    .orElse("");
        } else {
            return experimentSnapshotRefDao.getByExperimentId(key.getAppId(), key.getSnapshotType().getCode().toLowerCase(), key.getExperimentInstanceId(),
                            ExperimentSnapshotRefEntity::getExperimentInstanceId,
                            ExperimentSnapshotRefEntity::getRefExperimentInstanceId)
                    .map(i->ShareUtil.XString.defaultIfEmpty(i.getRefExperimentInstanceId(), i.getExperimentInstanceId()))
                    .orElse("");
        }
    }

    @Data
    @Builder
    @Accessors(chain = true)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnapshotCacheKey{
        @Schema(title = "应用id")
        private String appId;

        @Schema(title = "应用id")
        private EnumSnapshotType snapshotType;

        @Schema(title = "MD5")
        private String md5;

        @Schema(title = "实验实例id")
        private String experimentInstanceId;


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SnapshotCacheKey that = (SnapshotCacheKey) o;
            return Objects.equals(appId, that.appId) && Objects.equals(snapshotType, that.snapshotType) && Objects.equals(md5, that.md5) && Objects.equals(experimentInstanceId, that.experimentInstanceId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(appId, snapshotType, md5, experimentInstanceId);
        }

        @Override
        public String toString() {
            return String.format("%s-%s-%s-%s", appId,experimentInstanceId,snapshotType, md5);
        }
    }


}
