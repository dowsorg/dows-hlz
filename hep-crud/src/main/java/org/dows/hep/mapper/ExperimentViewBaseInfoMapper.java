package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentViewBaseInfoEntity;

/**
 * @author jx
 * @date 2023/6/13 16:47
 */
@Mapper
public interface ExperimentViewBaseInfoMapper extends MybatisCrudMapper<ExperimentViewBaseInfoEntity> {
}
