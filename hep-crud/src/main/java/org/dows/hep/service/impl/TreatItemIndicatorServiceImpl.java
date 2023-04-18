package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.TreatItemIndicatorMapper;
import org.dows.hep.entity.TreatItemIndicatorEntity;
import org.dows.hep.service.TreatItemIndicatorService;
import org.springframework.stereotype.Service;


/**
 * 治疗项目关联指标(TreatItemIndicator)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 14:00:03
 */
@Service("treatItemIndicatorService")
public class TreatItemIndicatorServiceImpl extends MybatisCrudServiceImpl<TreatItemIndicatorMapper, TreatItemIndicatorEntity> implements TreatItemIndicatorService {

}

