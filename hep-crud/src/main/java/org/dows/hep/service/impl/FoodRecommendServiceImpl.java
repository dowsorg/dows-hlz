package org.dows.hep.service.impl;

import org.dows.framework.crud.mybatis.MybatisCrudServiceImpl;
import org.dows.hep.mapper.FoodRecommendMapper;
import org.dows.hep.entity.FoodRecommendEntity;
import org.dows.hep.service.FoodRecommendService;
import org.springframework.stereotype.Service;


/**
 * 食物推荐量配置(FoodRecommend)表服务实现类
 *
 * @author lait
 * @since 2023-04-18 13:57:07
 */
@Service("foodRecommendService")
public class FoodRecommendServiceImpl extends MybatisCrudServiceImpl<FoodRecommendMapper, FoodRecommendEntity> implements FoodRecommendService {

}

