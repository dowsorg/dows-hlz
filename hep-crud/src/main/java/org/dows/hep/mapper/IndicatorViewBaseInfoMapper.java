package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.IndicatorViewBaseInfoEntity;

/**
 * 查看指标基本信息类(IndicatorViewBaseInfo)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:57:51
 */
@Mapper
public interface IndicatorViewBaseInfoMapper extends MybatisCrudMapper<IndicatorViewBaseInfoEntity> {

}

