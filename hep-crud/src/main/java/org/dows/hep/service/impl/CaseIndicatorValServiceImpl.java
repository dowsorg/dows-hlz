package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CaseIndicatorValEntity;
import org.dows.hep.entity.IndicatorValEntity;
import org.dows.hep.mapper.CaseIndicatorValMapper;
import org.dows.hep.mapper.IndicatorValMapper;
import org.dows.hep.service.CaseIndicatorValService;
import org.springframework.stereotype.Service;


/**
 * 指标值(IndicatorVal)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:43
 */
@Service("caseIndicatorValService")
public class CaseIndicatorValServiceImpl extends MybatisCrudServiceImpl<CaseIndicatorValMapper, CaseIndicatorValEntity> implements CaseIndicatorValService {

}

