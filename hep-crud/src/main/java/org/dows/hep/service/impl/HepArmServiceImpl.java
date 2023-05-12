package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.HepArmEntity;
import org.dows.hep.mapper.HepArmMapper;
import org.dows.hep.service.HepArmService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/5/12 10:41
 */
@Service("hepArmService")
public class HepArmServiceImpl extends MybatisCrudServiceImpl<HepArmMapper, HepArmEntity> implements HepArmService {
}
