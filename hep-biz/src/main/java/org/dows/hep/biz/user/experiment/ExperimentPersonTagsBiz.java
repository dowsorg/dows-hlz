package org.dows.hep.biz.user.experiment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.user.experiment.request.ExperimentIndicatorInstanceRequest;
import org.dows.hep.api.user.experiment.response.EchartsDataResonse;
import org.dows.hep.entity.ExperimentPersonEntity;
import org.dows.hep.entity.ExperimentPersonTagsEntity;
import org.dows.hep.entity.TagsInstanceEntity;
import org.dows.hep.service.ExperimentPersonService;
import org.dows.hep.service.ExperimentPersonTagsService;
import org.dows.hep.service.TagsInstanceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jx
 * @date 2023/7/13 17:09
 */
@RequiredArgsConstructor
@Service
public class ExperimentPersonTagsBiz {
    private final ExperimentPersonService experimentPersonService;
    private final ExperimentPersonTagsService experimentPersonTagsService;
    private final TagsInstanceService tagsInstanceService;

    /**
     * @param
     * @return
     * @说明: 实验疾病类别统计
     * @关联表: experiment_person_tags、experiment_person
     * @工时: 3H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/7/13 13:51
     */
    public List<EchartsDataResonse> statDiseaseRate(ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
        List<EchartsDataResonse> statList = new ArrayList<>();
        //1、根据实验实例ID、小组ID以及机构ID获取对应的人物列表
        LambdaQueryWrapper<ExperimentPersonEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ExperimentPersonEntity::getExperimentInstanceId, experimentIndicatorInstanceRequest.getExperimentInstanceId())
                .eq(ExperimentPersonEntity::getExperimentGroupId, experimentIndicatorInstanceRequest.getExperimentGroupId())
                .eq(ExperimentPersonEntity::getExperimentOrgId, experimentIndicatorInstanceRequest.getExperimentOrgId())
                .eq(ExperimentPersonEntity::getDeleted, false)
                .orderByDesc(ExperimentPersonEntity::getDt);
        List<ExperimentPersonEntity> personEntities = experimentPersonService.list(queryWrapper);
        //2、查询上述人物在实验人物标签中的标签信息
        //2.1、先获取上述人物的标签列表
        List<String> personIdList = personEntities.stream().map(e -> e.getExperimentPersonId()).collect(Collectors.toList());
        List<ExperimentPersonTagsEntity> tagsEntities = experimentPersonTagsService.lambdaQuery()
                .in(ExperimentPersonTagsEntity::getExperimentPersonId, personIdList)
                .eq(ExperimentPersonTagsEntity::getDeleted, false)
                .list();
        //2.2、根据experimentPersonId去重获取总人数
        List<ExperimentPersonTagsEntity> personList = tagsEntities.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(ExperimentPersonTagsEntity::getExperimentPersonId))), ArrayList::new));
        if (tagsEntities != null && tagsEntities.size() > 0) {
            //2.3、根据类别对数据分组
            Map<String, List<ExperimentPersonTagsEntity>> map = tagsEntities.stream().collect(Collectors.groupingBy(ExperimentPersonTagsEntity::getTagsId));
            Iterator<Map.Entry<String, List<ExperimentPersonTagsEntity>>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, List<ExperimentPersonTagsEntity>> next = iterator.next();
                //2.4、根据tagsId找到tagsName
                TagsInstanceEntity instanceEntity = tagsInstanceService.lambdaQuery()
                        .eq(TagsInstanceEntity::getTagsId, next.getKey())
                        .eq(TagsInstanceEntity::getDeleted, false)
                        .one();
                EchartsDataResonse stat = new EchartsDataResonse(instanceEntity.getName(), Long.valueOf(personList.size()), String.format("%.2f", (float) (long) next.getValue().size() / tagsEntities.size()));
                statList.add(stat);
            }
        }
        return statList;
    }
}
