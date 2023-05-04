package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseInstanceMapper;
import org.dows.hep.entity.CaseInstanceEntity;
import org.dows.hep.service.CaseInstanceService;
import org.springframework.stereotype.Service;


/**
 * 案例实例(CaseInstance)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:07
 */
@Service("caseInstanceService")
public class CaseInstanceServiceImpl extends MybatisCrudServiceImpl<CaseInstanceMapper, CaseInstanceEntity> implements CaseInstanceService {

}

