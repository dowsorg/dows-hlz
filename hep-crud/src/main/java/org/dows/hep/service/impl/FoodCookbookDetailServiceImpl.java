package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodCookbookDetailMapper;
import org.dows.hep.entity.FoodCookbookDetailEntity;
import org.dows.hep.service.FoodCookbookDetailService;
import org.springframework.stereotype.Service;


/**
 * 食谱食材(FoodCookbookDetail)表服务实现类
 *
 * @author lait
 * @since 2023-04-28 10:31:12
 */
@Service("foodCookbookDetailService")
public class FoodCookbookDetailServiceImpl extends MybatisCrudServiceImpl<FoodCookbookDetailMapper, FoodCookbookDetailEntity> implements FoodCookbookDetailService {

}

