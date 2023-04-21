package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseSchemeResultMapper;
import org.dows.hep.entity.CaseSchemeResultEntity;
import org.dows.hep.service.CaseSchemeResultService;
import org.springframework.stereotype.Service;


/**
 * 案例方案结果(CaseSchemeResult)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:41
 */
@Service("caseSchemeResultService")
public class CaseSchemeResultServiceImpl extends MybatisCrudServiceImpl<CaseSchemeResultMapper, CaseSchemeResultEntity> implements CaseSchemeResultService {

}

