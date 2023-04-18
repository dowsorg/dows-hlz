package org.dows.hep.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dows.framework.crud.mybatis.MybatisCrudMapper;
import org.dows.hep.entity.OperateTransfersEntity;

/**
 * 操作机构转入转出记录(OperateTransfers)表数据库访问层
 *
 * @author lait
 * @since 2023-04-18 13:58:57
 */
@Mapper
public interface OperateTransfersMapper extends MybatisCrudMapper<OperateTransfersEntity> {

}

