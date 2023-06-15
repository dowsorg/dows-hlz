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
import org.dows.hep.api.base.tags.request.PageTagsRequest;
import org.dows.hep.api.base.tags.request.TagsInstanceRequest;
import org.dows.hep.api.base.tags.response.TagsInstanceResponse;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.entity.TagsInstanceEntity;
import org.dows.hep.service.TagsInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author jx
 * @date 2023/6/14 15:25
 */
@Service
@RequiredArgsConstructor
public class TagsManageBiz {

    private final TagsInstanceService tagsInstanceService;

    private final IdGenerator idGenerator;

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
        if (manageRequest.getId() != null) {
            TagsInstanceEntity manageEntity = TagsInstanceEntity
                    .builder()
                    .id(manageRequest.getId())
                    .tagsId(manageRequest.getTagsId())
                    .appId(manageRequest.getAppId())
                    .name(manageRequest.getName())
                    .tagsFormulaId(manageRequest.getTagsFormulaId())
                    .tagsCategoryId(manageRequest.getTagsCategoryId())
                    .status(manageRequest.getStatus())
                    .build();
            flag = tagsInstanceService.updateById(manageEntity);
        } else {
            TagsInstanceEntity manageEntity = TagsInstanceEntity
                    .builder()
                    .tagsId(idGenerator.nextIdStr())
                    .appId(manageRequest.getAppId())
                    .name(manageRequest.getName())
                    .tagsFormulaId(manageRequest.getTagsFormulaId())
                    .tagsCategoryId(manageRequest.getTagsCategoryId())
                    .status(manageRequest.getStatus())
                    .build();
            flag = tagsInstanceService.save(manageEntity);
        }
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
        TagsInstanceResponse response = TagsInstanceResponse.builder()
                .appId(instanceEntity.getAppId())
                .name(instanceEntity.getName())
                .tagsFormulaId(instanceEntity.getTagsFormulaId())
                .tagsCategoryId(instanceEntity.getTagsCategoryId())
                .status(instanceEntity.getStatus())
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
            if (!StrUtil.isBlank(pageTagsRequest.getKeyword()) || !StrUtil.isBlank(pageTagsRequest.getTagsCategoryId())) {
                page = tagsInstanceService.page(page, tagsInstanceService.lambdaQuery()
                        .like(StringUtils.isNotEmpty(pageTagsRequest.getKeyword()),TagsInstanceEntity::getName, pageTagsRequest.getKeyword())
                        .like(StringUtils.isNotEmpty(pageTagsRequest.getTagsCategoryId()),TagsInstanceEntity::getTagsCategoryId, pageTagsRequest.getTagsCategoryId())
                        .getWrapper());
            } else {
                page = tagsInstanceService.page(page, tagsInstanceService.lambdaQuery().getWrapper());
            }
        } catch (Exception e) {
            throw new ExperimentException(e.getCause().getMessage());
        }
        PageResponse pageInfo = tagsInstanceService.getPageInfo(page, TagsInstanceResponse.class);
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
    public Boolean updateTagsByTagsId(Integer status, String tagsId) {
        LambdaUpdateWrapper<TagsInstanceEntity> updateWrapper = new LambdaUpdateWrapper<TagsInstanceEntity>()
                .eq(TagsInstanceEntity::getTagsId, tagsId)
                .eq(TagsInstanceEntity::getDeleted, false)
                .set(TagsInstanceEntity::getStatus, status);
        return tagsInstanceService.update(updateWrapper);
    }
}
