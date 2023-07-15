package org.dows.hep.biz.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.ExperimentCrudEntity;
import org.dows.hep.biz.util.CopyWrapper;
import org.dows.hep.biz.util.ShareUtil;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/6/27 15:18
 */
@Slf4j
public abstract class BaseSnapshotTableWriter<ST,ET extends ExperimentCrudEntity, ES extends MybatisCrudService<ET>>
        extends BaseSnapshotWriter<List<ST>> {
    protected BaseSnapshotTableWriter(EnumSnapshotType snapshotType, Supplier<ET> snapItemCreator){
        this.snapshotType=snapshotType;
        this.snapItemCreator=snapItemCreator;

    }

    protected final EnumSnapshotType snapshotType;
    protected final Supplier<ET> snapItemCreator;

    @Autowired
    protected ES experimentService;

    @Override
    public EnumSnapshotType getSnapshotType() {
        return snapshotType;
    }

    @Override
    protected boolean saveSnapshotData(SnapshotRequest req, List<ST> data) {
        if(ShareUtil.XObject.isEmpty(data)){
            return true;
        }
        List<ET> dstRows=new ArrayList<>();
        data.forEach(i->dstRows.add(castExperimentEntity(req,i)));
        final String experimentId=dstRows.get(0).getExperimentInstanceId();
        experimentService.update()
                .eq(snapshotType.getColExperimentInstanceId(), experimentId)
                .remove();
        return experimentService.saveBatch(dstRows) ;
    }

    protected ET castExperimentEntity(SnapshotRequest req,ST source){
        ET dst=CopyWrapper.create(snapItemCreator.get()).endFrom(source);
        dst.setExperimentInstanceId(req.getExperimentInstanceId());
        dst.setId(null);
        return dst;
    }


}
