package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentIndicatorViewBaseInfoRsEntity;

/**
 * 查看指标基本信息类(ExperimentIndicatorViewBaseInfoMapper)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:50
 */
@Mapper
public interface ExperimentIndicatorViewBaseInfoRsMapper extends MybatisCrudMapper<ExperimentIndicatorViewBaseInfoRsEntity> {

}

