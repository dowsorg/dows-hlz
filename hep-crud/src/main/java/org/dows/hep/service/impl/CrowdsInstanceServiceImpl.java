package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.mapper.CrowdsInstanceMapper;
import org.dows.hep.service.CrowdsInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/15 13:55
 */
@Service("crowdsInstanceService")
public class CrowdsInstanceServiceImpl extends MybatisCrudServiceImpl<CrowdsInstanceMapper, CrowdsInstanceEntity> implements CrowdsInstanceService {
}
