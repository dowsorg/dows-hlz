package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewSupportExamRefMapper;
import org.dows.hep.entity.IndicatorViewSupportExamRefEntity;
import org.dows.hep.service.IndicatorViewSupportExamRefService;
import org.springframework.stereotype.Service;


/**
 * 查看指标辅助检查关联指标(IndicatorViewSupportExamRef)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:58:27
 */
@Service("indicatorViewSupportExamRefService")
public class IndicatorViewSupportExamRefServiceImpl extends MybatisCrudServiceImpl<IndicatorViewSupportExamRefMapper, IndicatorViewSupportExamRefEntity> implements IndicatorViewSupportExamRefService {

}

