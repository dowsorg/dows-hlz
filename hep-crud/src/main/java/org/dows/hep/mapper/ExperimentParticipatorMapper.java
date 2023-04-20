package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentParticipatorEntity;

/**
 * 实验组员（参与者）(ExperimentParticipator)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:55:44
 */
@Mapper
public interface ExperimentParticipatorMapper extends MybatisCrudMapper<ExperimentParticipatorEntity> {

}

