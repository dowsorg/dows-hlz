package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentFollowupPlanEntity;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:07
 */
@Mapper
public interface ExperimentFollowupPlanMapper extends MybatisCrudMapper<ExperimentFollowupPlanEntity> {
}
