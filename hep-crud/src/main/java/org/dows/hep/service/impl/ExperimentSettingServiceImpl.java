package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentSettingMapper;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentSettingService;
import org.springframework.stereotype.Service;


/**
 * 实验设置(ExperimentSetting)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:42
 */
@Service("experimentSettingService")
public class ExperimentSettingServiceImpl extends MybatisCrudServiceImpl<ExperimentSettingMapper, ExperimentSettingEntity> implements ExperimentSettingService {

}

