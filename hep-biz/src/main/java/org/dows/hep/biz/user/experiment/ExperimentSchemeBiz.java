package org.dows.hep.biz.user.experiment;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.AllArgsConstructor;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeItemRequest;
import org.dows.hep.api.user.experiment.request.ExperimentSchemeRequest;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.entity.ExperimentSchemeEntity;
import org.dows.hep.service.ExperimentSchemeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lait.zhang
 * @description project descr:实验:实验方案
 * @date 2023年4月23日 上午9:44:34
 */
@AllArgsConstructor
@Service
public class ExperimentSchemeBiz {
    private final ExperimentSchemeService experimentSchemeService;
    private final ExperimentSchemeItemBiz experimentSchemeItemBiz;

    /**
     * @param
     * @return
     * @说明: 获取实验方案
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    public ExperimentSchemeResponse getScheme(String experimentInstanceId, String experimentGroupId, String accountId) {
        if (StrUtil.isBlank(experimentGroupId) || StrUtil.isBlank(experimentInstanceId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        ExperimentSchemeEntity entity = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentSchemeEntity::getExperimentGroupId, experimentGroupId)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
        ExperimentSchemeResponse result = BeanUtil.copyProperties(entity, ExperimentSchemeResponse.class);

        List<ExperimentSchemeItemResponse> itemList = experimentSchemeItemBiz.listBySchemeId(entity.getExperimentSchemeId());
        setAuthority(itemList, accountId);
        result.setItemList(itemList);

        return result;
    }

    /**
     * @author fhb
     * @description 保存
     * @date 2023/6/7 13:50
     * @param
     * @return
     */
    public Boolean updateScheme(ExperimentSchemeRequest request) {
        if (BeanUtil.isEmpty(request)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        checkState(request.getExperimentSchemeId());

        List<ExperimentSchemeItemRequest> itemList = request.getItemList();
        return experimentSchemeItemBiz.updateBatch(itemList);
    }

    /**
     * @param
     * @return
     * @说明: 提交
     * @关联表:
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean submitScheme(String experimentSchemeId) {
        if (StrUtil.isBlank(experimentSchemeId)) {
            throw new BizException(ExperimentESCEnum.PARAMS_NON_NULL);
        }

        checkState(experimentSchemeId);

        return experimentSchemeService.lambdaUpdate()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, experimentSchemeId)
                .set(ExperimentSchemeEntity::getState, 1)
                .update();
    }

    private void checkState(String request) {
        ExperimentSchemeEntity schemeEntity = experimentSchemeService.lambdaQuery()
                .eq(ExperimentSchemeEntity::getExperimentSchemeId, request)
                .oneOpt()
                .orElseThrow(() -> new BizException(ExperimentESCEnum.SCHEME_NOT_NULL));
        Integer state = schemeEntity.getState();
        if (state == 1) {
            throw new BizException(ExperimentESCEnum.SCHEME_HAS_BEEN_SUBMITTED);
        }
    }

    private void setAuthority(List<ExperimentSchemeItemResponse> itemList, String accountId) {
        if (CollUtil.isEmpty(itemList)) {
            return;
        }

        itemList.forEach(item -> {
            String accountId1 = item.getAccountId();
            if (accountId.equals(accountId1)) {
                item.setCanEdit(Boolean.TRUE);
            } else {
                item.setCanEdit(Boolean.FALSE);
            }
        });
    }
}