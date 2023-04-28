package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseEventActionIndicatorEntity;

/**
 * 人物事件处理选项影响指标(CaseEventActionIndicator)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:43
 */
@Mapper
public interface CaseEventActionIndicatorMapper extends MybatisCrudMapper<CaseEventActionIndicatorEntity> {

}

