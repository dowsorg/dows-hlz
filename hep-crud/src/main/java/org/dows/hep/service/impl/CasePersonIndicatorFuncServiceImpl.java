package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.CasePersonIndicatorFuncEntity;
import org.dows.hep.mapper.CasePersonIndicatorFuncMapper;
import org.dows.hep.service.CasePersonIndicatorFuncService;
import org.springframework.stereotype.Service;

/**
 * 人物功能点(CasePersonIndicatorFunc)表服务实现类
 *
 * @author lait
 * @since 2023-04-27 10:12:41
 */
@Service("casePersonIndicatorFuncService")
public class CasePersonIndicatorFuncServiceImpl extends MybatisCrudServiceImpl<CasePersonIndicatorFuncMapper, CasePersonIndicatorFuncEntity> implements CasePersonIndicatorFuncService {
}
