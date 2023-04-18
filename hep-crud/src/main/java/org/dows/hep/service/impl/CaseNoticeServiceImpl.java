package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.CaseNoticeMapper;
import org.dows.hep.entity.CaseNoticeEntity;
import org.dows.hep.service.CaseNoticeService;
import org.springframework.stereotype.Service;


/**
 * 案例公告(CaseNotice)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:54:28
 */
@Service("caseNoticeService")
public class CaseNoticeServiceImpl extends MybatisCrudServiceImpl<CaseNoticeMapper, CaseNoticeEntity> implements CaseNoticeService {

}

