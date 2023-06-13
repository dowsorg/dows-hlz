package org.dows.hep.biz.user.experiment;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.crud.api.model.PageResponse;
import org.dows.framework.crud.mybatis.utils.BeanConvert;
import org.dows.hep.api.enums.ExperimentStatusCode;
import org.dows.hep.api.enums.ParticipatorTypeEnum;
import org.dows.hep.api.exception.ExperimentException;
import org.dows.hep.api.tenant.experiment.request.PageExperimentRequest;
import org.dows.hep.api.tenant.experiment.response.ExperimentListResponse;
import org.dows.hep.api.user.experiment.request.GetExperimentGroupCaptainRequest;
import org.dows.hep.api.user.experiment.response.GetExperimentGroupCaptainResponse;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Slf4j
@RequiredArgsConstructor
@Service
public class ExperimentParticipatorBiz {
    // 实验参与者
    private final ExperimentParticipatorService experimentParticipatorService;
    // 实验小组
    private final ExperimentGroupService experimentGroupService;

    /**
     * @param
     * @return
     * @说明: 学生端分页实验列表
     * @关联表: ExperimentInstance
     * @工时: 2H
     * @开发者: lait
     * @开始时间:
     * @创建时间: 2023年4月18日 上午10:45:07
     */
    public PageResponse<ExperimentListResponse> page(PageExperimentRequest pageExperimentRequest) {
        Page page = new Page<ExperimentParticipatorEntity>();
        page.setCurrent(pageExperimentRequest.getPageNo());
        page.setSize(pageExperimentRequest.getPageSize());

        if (pageExperimentRequest.getOrder() != null) {
            String[] array = (String[]) pageExperimentRequest.getOrder().stream()
                    .map(s -> StrUtil.toUnderlineCase((CharSequence) s))
                    .toArray(String[]::new);
            page.addOrder(pageExperimentRequest.getDesc() ? OrderItem.descs(array) : OrderItem.ascs(array));
        }
        if (!StrUtil.isBlank(pageExperimentRequest.getAccountId())) {
            page = experimentParticipatorService.page(page, experimentParticipatorService.lambdaQuery()
                    .eq(ExperimentParticipatorEntity::getAccountId, pageExperimentRequest.getAccountId())
                    .getWrapper());
        } else {
            page = page.setTotal(0).setCurrent(0).setSize(0).setRecords(new ArrayList<>());
        }
        PageResponse pageInfo = experimentParticipatorService.getPageInfo(page, ExperimentListResponse.class);
        return pageInfo;
    }

    /**
     * 获取实验小组长
     *
     * @param getExperimentGroupCaptainRequest
     * @return
     */
    public GetExperimentGroupCaptainResponse getExperimentGroupCaptain(GetExperimentGroupCaptainRequest getExperimentGroupCaptainRequest) {

        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, getExperimentGroupCaptainRequest.getExperimentInstanceId())
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, getExperimentGroupCaptainRequest.getExperimentGroupId())
                .eq(ExperimentParticipatorEntity::getAccountId, getExperimentGroupCaptainRequest.getAccountId())
//                .eq(ExperimentParticipatorEntity::getParticipatorType, getExperimentGroupCaptainRequest.getParticipatorType().getCode())
                .oneOpt().orElse(null);
        if (experimentParticipatorEntity == null) {
            throw new ExperimentException(ExperimentStatusCode.NOT_CAPTAIN);
//            return null;
        }
        return BeanConvert.beanConvert(experimentParticipatorEntity, GetExperimentGroupCaptainResponse.class);
    }

    /**
     * @author fhb
     * @description 是否是组长
     * @date 2023/6/13 10:48
     * @param
     * @return
     */
    public Boolean isCaptain(String experimentInstanceId, String experimentGroupId, String accountId) {
        Long count = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, experimentInstanceId)
                .eq(ExperimentParticipatorEntity::getExperimentGroupId, experimentGroupId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .eq(ExperimentParticipatorEntity::getParticipatorType, ParticipatorTypeEnum.CAPTAIN.getCode())
                .count();
        if (count == null || count == 0) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
