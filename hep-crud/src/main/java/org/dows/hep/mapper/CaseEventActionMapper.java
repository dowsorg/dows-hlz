package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseEventActionEntity;

/**
 * 案例人物事件处理选项(CaseEventAction)表数据库访问层
 *
 * @author lait
 * @since 2023-04-21 10:31:14
 */
@Mapper
public interface CaseEventActionMapper extends MybatisCrudMapper<CaseEventActionEntity> {

}

