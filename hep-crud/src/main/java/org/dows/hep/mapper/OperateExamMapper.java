package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateExamEntity;

/**
 * 操作考试[题目]记录(OperateExam)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:43
 */
@Mapper
public interface OperateExamMapper extends MybatisCrudMapper<OperateExamEntity> {

}

