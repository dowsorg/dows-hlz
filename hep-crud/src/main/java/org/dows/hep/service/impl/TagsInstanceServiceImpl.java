package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.entity.TagsInstanceEntity;
import org.dows.hep.mapper.TagsInstanceMapper;
import org.dows.hep.service.TagsInstanceService;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/14 15:15
 */
@Service("tagsInstanceService")
public class TagsInstanceServiceImpl extends MybatisCrudServiceImpl<TagsInstanceMapper, TagsInstanceEntity> implements TagsInstanceService {
}
