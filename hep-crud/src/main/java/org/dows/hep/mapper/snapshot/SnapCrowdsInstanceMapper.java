package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapCrowdsInstanceEntity;


/**
 * @author : wuzl
 * @date : 2023/9/14 9:54
 */

@Mapper
public interface SnapCrowdsInstanceMapper extends MybatisCrudMapper<SnapCrowdsInstanceEntity> {
}
