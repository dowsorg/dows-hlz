package org.dows.hep.biz.snapshot;

import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.mybatis.MybatisCrudService;
import org.dows.hep.ExperimentCrudEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.function.Supplier;

/**
 * @author : wuzl
 * @date : 2023/6/27 15:18
 */
@Slf4j
public class BaseSnapshotFullTableWriter<ST,SS extends MybatisCrudService<ST>, ET extends ExperimentCrudEntity, ES extends MybatisCrudService<ET>>
        extends BaseSnapshotTableWriter<ST,ET,ES > {

    protected BaseSnapshotFullTableWriter(EnumSnapshotType snapshotType, Supplier<ET> snapItemCreator) {
        super(snapshotType, snapItemCreator);
    }


    @Autowired
    protected SS sourceService;


    @Override
    public List<ST> readSource(SnapshotRequest req) {
        return sourceService.query()
                .eq("app_id", req.getAppId())
                .orderByAsc("id")
                .list();
    }
}
