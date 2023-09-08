package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentEvalLogContentEntity;

/**
 * @author : wuzl
 * @date : 2023/9/6 14:13
 */

@Mapper
public interface ExperimentEvalLogContentMapper extends MybatisCrudMapper<ExperimentEvalLogContentEntity> {
}
