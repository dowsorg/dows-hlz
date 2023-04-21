package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.MaterialsAttachmentMapper;
import org.dows.hep.entity.MaterialsAttachmentEntity;
import org.dows.hep.service.MaterialsAttachmentService;
import org.springframework.stereotype.Service;


/**
 * 资料-附件(MaterialsAttachment)表服务实现类
 *
 * @author lait
 * @since 2023-04-21 10:31:44
 */
@Service("materialsAttachmentService")
public class MaterialsAttachmentServiceImpl extends MybatisCrudServiceImpl<MaterialsAttachmentMapper, MaterialsAttachmentEntity> implements MaterialsAttachmentService {

}

