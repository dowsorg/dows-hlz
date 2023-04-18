package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseSettingEntity;

/**
 * 案例问卷设置(CaseSetting)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:54:32
 */
@Mapper
public interface CaseSettingMapper extends MybatisCrudMapper<CaseSettingEntity> {

}

