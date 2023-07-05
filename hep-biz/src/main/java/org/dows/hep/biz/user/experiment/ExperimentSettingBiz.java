package org.dows.hep.biz.user.experiment;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExperimentESCEnum;
import org.dows.hep.entity.ExperimentSettingEntity;
import org.dows.hep.service.ExperimentSettingService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Optional;

/**
 * 实验设置BIZ
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ExperimentSettingBiz {
    private final ExperimentSettingService experimentSettingService;

    /**
     * 根据实验实例ID获取实验 `方案设计` 配置信息
     *
     * @param exptInstanceId - 实验实例ID
     * @return `方案设计` 配置信息(JSON格式)
     * @date 2023/7/5 15:24
     */
    public String getSchemeSettingStr(String exptInstanceId) {
        return Optional.ofNullable(getSchemeSetting0(exptInstanceId))
                .map(ExperimentSettingEntity::getConfigJsonVals)
                .orElse("");
    }

    /**
     * 根据实验实例ID获取实验 `沙盘` 配置信息
     *
     * @param exptInstanceId - 实验实例ID
     * @return `沙盘` 配置信息（JSON格式）
     * @date 2023/7/5 15:24
     */
    public String getSandSettingStr(String exptInstanceId) {
        return Optional.ofNullable(getSandSetting0(exptInstanceId))
                .map(ExperimentSettingEntity::getConfigJsonVals)
                .orElse("");
    }

    /**
     * 根据实验实例ID获取实验 `方案设计` 配置信息
     *
     * @param exptInstanceId - 实验实例ID
     * @return `方案设计` 配置信息(Bean格式)
     * @date 2023/7/5 15:24
     */
    public ExperimentSetting.SchemeSetting getSchemeSetting(String exptInstanceId) {
        String settingStr = getSchemeSettingStr(exptInstanceId);
        return JSONUtil.toBean(settingStr, ExperimentSetting.SchemeSetting.class);
    }

    /**
     * 根据实验实例ID获取实验 `沙盘` 配置信息
     *
     * @param exptInstanceId - 实验实例ID
     * @return `沙盘` 配置信息（Bean格式）
     * @date 2023/7/5 15:24
     */
    public ExperimentSetting.SandSetting getSandSetting(String exptInstanceId) {
        String settingStr = getSandSettingStr(exptInstanceId);
        return JSONUtil.toBean(settingStr, ExperimentSetting.SandSetting.class);
    }

    /**
     * 根据实验实例ID获取实验是否包含 `方案设计` 模式
     *
     * @param exptInstanceId - 实验实例ID
     * @return boolean
     * @date 2023/7/5 15:24
     */
    public boolean containsScheme(String exptInstanceId) {
        String settingStr = getSchemeSettingStr(exptInstanceId);
        if (StrUtil.isNotBlank(settingStr)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据实验实例ID获取实验是否包含 `沙盘` 模式
     *
     * @param exptInstanceId - 实验实例ID
     * @return boolean
     * @date 2023/7/5 15:24
     */
    public boolean containsSand(String exptInstanceId) {
        String settingStr = getSandSettingStr(exptInstanceId);
        if (StrUtil.isNotBlank(settingStr)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    private ExperimentSettingEntity getSchemeSetting0(String exptInstanceId) {
        List<ExperimentSettingEntity> settingList = listExptSetting(exptInstanceId);
        if (CollUtil.isEmpty(settingList)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }

        return settingList.stream()
                .filter(item -> ExperimentSetting.SchemeSetting.class.getName().equals(item.getConfigKey()))
                .findFirst()
                .orElse(null);
    }

    private ExperimentSettingEntity getSandSetting0(String exptInstanceId) {
        List<ExperimentSettingEntity> settingList = listExptSetting(exptInstanceId);
        if (CollUtil.isEmpty(settingList)) {
            throw new BizException(ExperimentESCEnum.DATA_NULL);
        }

        return settingList.stream()
                .filter(item -> ExperimentSetting.SandSetting.class.getName().equals(item.getConfigKey()))
                .findFirst()
                .orElse(null);
    }

    private List<ExperimentSettingEntity> listExptSetting(String exptInstanceId) {
        Assert.notNull(exptInstanceId, "查询实验设置时，实验ID不能为空");

        return experimentSettingService.lambdaQuery()
                .eq(ExperimentSettingEntity::getExperimentInstanceId, exptInstanceId)
                .list();
    }
}
