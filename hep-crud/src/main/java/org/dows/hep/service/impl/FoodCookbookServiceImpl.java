package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodCookbookMapper;
import org.dows.hep.entity.FoodCookbookEntity;
import org.dows.hep.service.FoodCookbookService;
import org.springframework.stereotype.Service;


/**
 * 食谱(FoodCookbook)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:56:26
 */
@Service("foodCookbookService")
public class FoodCookbookServiceImpl extends MybatisCrudServiceImpl<FoodCookbookMapper, FoodCookbookEntity> implements FoodCookbookService {

}

