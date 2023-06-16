package org.dows.hep.biz.base.tags;

import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.hep.api.base.indicator.request.BatchBindReasonIdRequestRs;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;
import org.dows.hep.api.base.tags.request.PageTagsRequest;
import org.dows.hep.api.base.tags.request.TagsInstanceRequest;
import org.dows.hep.api.base.tags.response.TagsInstanceResponse;
import org.dows.hep.api.enums.EnumIndicatorExpressionSource;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.biz.base.indicator.IndicatorExpressionBiz;
import org.dows.hep.entity.TagsInstanceEntity;
import org.dows.hep.service.TagsInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author jx
 * @date 2023/6/14 15:25
 */
@Service
@RequiredArgsConstructor
public class TagsManageBiz {

    private final TagsInstanceService tagsInstanceService;
    private final IdGenerator idGenerator;
    private final IndicatorExpressionBiz indicatorExpressionBiz;


    /**
     * @param
     * @return
     * @说明: 新增标签
     * @关联表: TagsInstance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月14日 下午15:26:34
     */
    @DSTransactional
    public Boolean insertOrUpdateTags(TagsInstanceRequest manageRequest) {
        Boolean flag = false;
        String tagsId = manageRequest.getTagsId();
        if (tagsId != null) {
            TagsInstanceEntity manageEntity = TagsInstanceEntity
                    .builder()
                    .id(manageRequest.getId())
                    .tagsId(tagsId)
                    .appId(manageRequest.getAppId())
                    .name(manageRequest.getName())
                    .tagsFormulaId(manageRequest.getTagsFormulaId())
                    .tagsCategoryId(manageRequest.getTagsCategoryId())
                    .status(manageRequest.getStatus())
                    .build();
            flag = tagsInstanceService.updateById(manageEntity);
        } else {
            tagsId = idGenerator.nextIdStr();
            TagsInstanceEntity manageEntity = TagsInstanceEntity
                    .builder()
                    .tagsId(tagsId)
                    .appId(manageRequest.getAppId())
                    .name(manageRequest.getName())
                    .tagsFormulaId(manageRequest.getTagsFormulaId())
                    .tagsCategoryId(manageRequest.getTagsCategoryId())
                    .status(manageRequest.getStatus())
                    .build();
            flag = tagsInstanceService.save(manageEntity);
        }
        List<String> indicatorExpressionIdList = new ArrayList<>();
        indicatorExpressionIdList.add(manageRequest.getTagsFormulaId());
        indicatorExpressionBiz.batchBindReasonId(BatchBindReasonIdRequestRs
            .builder()
            .reasonId(tagsId)
            .appId(manageRequest.getAppId())
            .source(EnumIndicatorExpressionSource.LABEL_MANAGEMENT.getType())
            .indicatorExpressionIdList(indicatorExpressionIdList)
            .build());
        return flag;
    }

    /**
     * @param
     * @return
     * @说明: 新增标签
     * @关联表: TagsInstance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月14日 下午15:26:34
     */
    public TagsInstanceResponse getTagsByTagsId(String tagsId) {
        TagsInstanceEntity instanceEntity = tagsInstanceService.lambdaQuery()
                .eq(TagsInstanceEntity::getTagsId, tagsId)
                .eq(TagsInstanceEntity::getDeleted, false)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.DATA_NULL));
        String appId = instanceEntity.getAppId();
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        indicatorInstanceIdSet.add(instanceEntity.getTagsId());
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
        List<IndicatorExpressionResponseRs> indicatorExpressionResponseRs = kReasonIdVIndicatorExpressionResponseRsListMap.get(instanceEntity.getTagsId());
        TagsInstanceResponse response = TagsInstanceResponse.builder()
            .id(instanceEntity.getId())
            .tagsId(instanceEntity.getTagsId())
            .appId(instanceEntity.getAppId())
            .name(instanceEntity.getName())
            .tagsFormulaId(instanceEntity.getTagsFormulaId())
            .tagsCategoryId(instanceEntity.getTagsCategoryId())
            .status(instanceEntity.getStatus())
            .indicatorExpressionResponseRsList(indicatorExpressionResponseRs)
            .build();
        return response;
    }

    /**
     * @param
     * @return
     * @说明: 分页获取标签列表
     * @关联表: TagsInstance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月14日 下午17:48:34
     */
    public PageResponse<TagsInstanceResponse> page(PageTagsRequest pageTagsRequest) {
        String appId = pageTagsRequest.getAppId();
        Page page = new Page<TagsInstanceEntity>();
        page.setSize(pageTagsRequest.getPageSize());
        page.setCurrent(pageTagsRequest.getPageNo());
        if (pageTagsRequest.getOrder() != null) {
            String[] array = (String[]) pageTagsRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageTagsRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        try {
            if (!StrUtil.isBlank(pageTagsRequest.getKeyword()) || !StrUtil.isBlank(pageTagsRequest.getTagsCategoryIds())) {
                page = tagsInstanceService.page(page, tagsInstanceService.lambdaQuery()
                        .like(StringUtils.isNotEmpty(pageTagsRequest.getKeyword()),TagsInstanceEntity::getName, pageTagsRequest.getKeyword())
                        .in(StringUtils.isNotEmpty(pageTagsRequest.getTagsCategoryIds()),TagsInstanceEntity::getTagsCategoryId,Arrays.asList(pageTagsRequest.getTagsCategoryIds().split(",")))
                        .getWrapper());
            } else {
                page = tagsInstanceService.page(page, tagsInstanceService.lambdaQuery().getWrapper());
            }
        } catch (Exception e) {
            throw new ExperimentException(e.getCause().getMessage());
        }
        Set<String> indicatorInstanceIdSet = new HashSet<>();
        Map<String, List<IndicatorExpressionResponseRs>> kReasonIdVIndicatorExpressionResponseRsListMap = new HashMap<>();
        PageResponse pageInfo = tagsInstanceService.getPageInfo(page, TagsInstanceResponse.class);
        List<TagsInstanceResponse> tagsInstanceResponseList = pageInfo.getList();
        if (Objects.nonNull(tagsInstanceResponseList) && !tagsInstanceResponseList.isEmpty()) {
            tagsInstanceResponseList.forEach(tagsInstanceResponse -> {
                indicatorInstanceIdSet.add(tagsInstanceResponse.getTagsId());
            });
            indicatorExpressionBiz.populateKReasonIdVIndicatorExpressionResponseRsListMap(appId, indicatorInstanceIdSet, kReasonIdVIndicatorExpressionResponseRsListMap);
            tagsInstanceResponseList.forEach(tagsInstanceResponse -> {
                String tagsId = tagsInstanceResponse.getTagsId();
                List<IndicatorExpressionResponseRs> indicatorExpressionResponseRsList = kReasonIdVIndicatorExpressionResponseRsListMap.get(tagsId);
                tagsInstanceResponse.setIndicatorExpressionResponseRsList(indicatorExpressionResponseRsList);
            });
        }
        pageInfo.setList(tagsInstanceResponseList);
        return pageInfo;
    }

    /**
     * @param
     * @return
     * @说明: 删除标签
     * @关联表: TagsInstance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 上午9:19:34
     */
    @DSTransactional
    public Boolean batchDelTags(Set<String> tagsIds) {
        LambdaUpdateWrapper<TagsInstanceEntity> updateWrapper = new LambdaUpdateWrapper<TagsInstanceEntity>()
                .in(TagsInstanceEntity::getTagsId, tagsIds)
                .eq(TagsInstanceEntity::getDeleted, false)
                .set(TagsInstanceEntity::getDeleted, true);
        return tagsInstanceService.update(updateWrapper);
    }

    /**
     * @param
     * @return
     * @说明: 更新标签
     * @关联表: TagsInstance
     * @工时: 1H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年6月15日 上午10:02:34
     */
    @DSTransactional
    public Boolean updateTagsByTagsId(Integer status,String tagsId) {
        LambdaUpdateWrapper<TagsInstanceEntity> updateWrapper = new LambdaUpdateWrapper<TagsInstanceEntity>()
                .eq(TagsInstanceEntity::getTagsId, tagsId)
                .eq(TagsInstanceEntity::getDeleted, false)
                .set(TagsInstanceEntity::getStatus, status);
        return tagsInstanceService.update(updateWrapper);
    }
}
