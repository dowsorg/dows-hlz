package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseNoticeEntity;

/**
 * 案例公告(CaseNotice)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:43
 */
@Mapper
public interface CaseNoticeMapper extends MybatisCrudMapper<CaseNoticeEntity> {

}

