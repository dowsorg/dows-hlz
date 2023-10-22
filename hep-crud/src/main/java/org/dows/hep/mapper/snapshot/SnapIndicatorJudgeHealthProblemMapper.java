package org.dows.hep.mapper.snapshot;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.snapshot.SnapIndicatorJudgeHealthProblemEntity;

/**
 * @author : wuzl
 * @date : 2023/10/21 17:07
 */
@Mapper
public interface SnapIndicatorJudgeHealthProblemMapper extends MybatisCrudMapper<SnapIndicatorJudgeHealthProblemEntity> {
}
