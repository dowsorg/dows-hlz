package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.IndicatorViewSupportExamMapper;
import org.dows.hep.entity.IndicatorViewSupportExamEntity;
import org.dows.hep.service.IndicatorViewSupportExamService;
import org.springframework.stereotype.Service;


/**
 * 查看指标辅助检查类(IndicatorViewSupportExam)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("indicatorViewSupportExamService")
public class IndicatorViewSupportExamServiceImpl extends MybatisCrudServiceImpl<IndicatorViewSupportExamMapper, IndicatorViewSupportExamEntity> implements IndicatorViewSupportExamService {

}

