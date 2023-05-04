package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.ExperimentOrgNoticeMapper;
import org.dows.hep.entity.ExperimentOrgNoticeEntity;
import org.dows.hep.service.ExperimentOrgNoticeService;
import org.springframework.stereotype.Service;


/**
 * 实验机构通知(ExperimentOrgNotice)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:10
 */
@Service("experimentOrgNoticeService")
public class ExperimentOrgNoticeServiceImpl extends MybatisCrudServiceImpl<ExperimentOrgNoticeMapper, ExperimentOrgNoticeEntity> implements ExperimentOrgNoticeService {

}

