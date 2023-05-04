package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.ExperimentQuestionItemEntity;

/**
 * 实验答题项目(ExperimentQuestionItem)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:47
 */
@Mapper
public interface ExperimentQuestionItemMapper extends MybatisCrudMapper<ExperimentQuestionItemEntity> {

}

