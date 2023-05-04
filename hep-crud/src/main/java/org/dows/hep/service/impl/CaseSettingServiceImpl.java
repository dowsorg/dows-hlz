package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseSettingMapper;
import org.dows.hep.entity.CaseSettingEntity;
import org.dows.hep.service.CaseSettingService;
import org.springframework.stereotype.Service;


/**
 * 案例问卷设置(CaseSetting)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:08
 */
@Service("caseSettingService")
public class CaseSettingServiceImpl extends MybatisCrudServiceImpl<CaseSettingMapper, CaseSettingEntity> implements CaseSettingService {

}

