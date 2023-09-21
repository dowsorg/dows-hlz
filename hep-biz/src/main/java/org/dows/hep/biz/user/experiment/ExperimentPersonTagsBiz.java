package org.dows.hep.biz.user.experiment;

import lombok.RequiredArgsConstructor;
import org.dows.framework.api.util.ReflectUtil;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.user.experiment.request.ExperimentIndicatorInstanceRequest;
import org.dows.hep.api.user.experiment.response.EchartsDataResonse;
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
    public List<EchartsDataResonse> statDiseaseRate(ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest){
        return new ArrayList<>();
    }
    public List<EchartsDataResonse> statDiseaseRateOld(ExperimentIndicatorInstanceRequest experimentIndicatorInstanceRequest) {
        List<EchartsDataResonse> statList = new ArrayList<>();
        //1、查询人物在实验人物标签中的标签信息
        //1.1、先获取上述人物的标签列表
        List<ExperimentPersonTagsEntity> tagsEntities = experimentPersonTagsService.lambdaQuery()
                .eq(ExperimentPersonTagsEntity::getExperimentPersonId, experimentIndicatorInstanceRequest.getExperimentPersonId())
                .eq(ExperimentPersonTagsEntity::getDeleted, false)
                .list();
        //1.2、根据experimentPersonId去重获取总人数
        List<ExperimentPersonTagsEntity> personList = tagsEntities.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                -> new TreeSet<>(Comparator.comparing(ExperimentPersonTagsEntity::getExperimentPersonId))), ArrayList::new));
        if (tagsEntities != null && tagsEntities.size() > 0) {
            //1.2、获取所有标签类别ID集合
            List<String> tagIds = new ArrayList<>();
            tagsEntities.forEach(person->{
                tagIds.addAll(Arrays.asList( person.getTagsIds().split(",")));
            });
            //1.3、list转map
            Map<String,Integer> map = new HashMap();
            for (String str : tagIds) {
                Object obj = map.get(str);
                if(obj != null){
                    map.put(str, ((Integer) obj + 1));
                }else{
                    map.put(str, 1);
                }
            }
            //1.4、根据类别对数据分组
            Iterator<Map.Entry<String, Integer>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Integer> next = iterator.next();
                //1.5、根据tagsId找到tagsName
                TagsInstanceEntity instanceEntity = tagsInstanceService.lambdaQuery()
                        .eq(TagsInstanceEntity::getTagsId, next.getKey())
                        .eq(TagsInstanceEntity::getDeleted, false)
                        .one();
                if(instanceEntity == null || ReflectUtil.isObjectNull(instanceEntity)){
                    throw new ExperimentException("id为" + next.getKey() + "标签不存在");
                }
                EchartsDataResonse stat = new EchartsDataResonse(instanceEntity.getName(), Long.valueOf(personList.size()), String.format("%.2f", (float) (long) next.getValue() / tagIds.size()));
                statList.add(stat);
            }
        }
        return statList;
    }
}
