package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CasePersonMapper;
import org.dows.hep.entity.CasePersonEntity;
import org.dows.hep.service.CasePersonService;
import org.springframework.stereotype.Service;


/**
 * 案例人物(CasePerson)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:31
 */
@Service("casePersonService")
public class CasePersonServiceImpl extends MybatisCrudServiceImpl<CasePersonMapper, CasePersonEntity> implements CasePersonService {

}

