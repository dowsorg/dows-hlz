package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewPhysicalExamRefMapper;
import org.dows.hep.entity.IndicatorViewPhysicalExamRefEntity;
import org.dows.hep.service.IndicatorViewPhysicalExamRefService;
import org.springframework.stereotype.Service;


/**
 * 查看指标体格检查关联指标(IndicatorViewPhysicalExamRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("indicatorViewPhysicalExamRefService")
public class IndicatorViewPhysicalExamRefServiceImpl extends MybatisCrudServiceImpl<IndicatorViewPhysicalExamRefMapper, IndicatorViewPhysicalExamRefEntity> implements IndicatorViewPhysicalExamRefService {

}

