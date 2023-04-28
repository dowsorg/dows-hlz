package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateFollowupTimerEntity;

/**
 * 学生随访操作计时器(OperateFollowupTimer)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:51
 */
@Mapper
public interface OperateFollowupTimerMapper extends MybatisCrudMapper<OperateFollowupTimerEntity> {

}

