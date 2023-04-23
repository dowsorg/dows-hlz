package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseEventActionIndicatorMapper;
import org.dows.hep.entity.CaseEventActionIndicatorEntity;
import org.dows.hep.service.CaseEventActionIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 人物事件处理选项影响指标(CaseEventActionIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:40
 */
@Service("caseEventActionIndicatorService")
public class CaseEventActionIndicatorServiceImpl extends MybatisCrudServiceImpl<CaseEventActionIndicatorMapper, CaseEventActionIndicatorEntity> implements CaseEventActionIndicatorService {

}

