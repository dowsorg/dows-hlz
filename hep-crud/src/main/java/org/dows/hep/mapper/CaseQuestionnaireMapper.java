package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.CaseQuestionnaireEntity;

/**
 * 案例问卷(CaseQuestionnaire)表数据库访问层
 *
 * @author lait
 * @since 2023-04-28 10:29:44
 */
@Mapper
public interface CaseQuestionnaireMapper extends MybatisCrudMapper<CaseQuestionnaireEntity> {

}

