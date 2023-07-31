package org.dows.hep.biz.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.constant.RedisKeyConst;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.enums.EnumExperimentMode;
import org.dows.hep.api.enums.EnumExperimentState;
import org.dows.hep.api.tenant.experiment.request.ExptAccountReportRequest;
import org.dows.hep.api.tenant.experiment.request.ExptGroupReportPageRequest;
import org.dows.hep.api.tenant.experiment.request.ExptReportPageRequest;
import org.dows.hep.api.tenant.experiment.response.ExptAccountReportResponse;
import org.dows.hep.api.tenant.experiment.response.ExptGroupReportPageResponse;
import org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse;
import org.dows.hep.api.user.experiment.ExptReportTypeEnum;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentRankingEntity;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentRankingService;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @version 1.0
 * @description 报告聚合 biz
 * @date 2023/7/21 13:57
 **/
@Component
@AllArgsConstructor
@Slf4j
public class ExptReportFacadeBiz {
    private final RedissonClient redissonClient;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentBaseBiz baseBiz;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentRankingService experimentRankingService;

    private final ExptSchemeReportHandler schemeReportHandler;
    private final ExptSandReportHandler sandReportHandler;
    private final ExptOverviewReportHandler overviewReportHandler;

    private final ReportZipHelper reportZipHelper;
    private final ReportRecordHelper reportRecordHelper;

    /**
     * @param pageRequest - 分页实验报告请求
     * @param accessAccountId - 访问账号
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse>
     * @author fhb
     * @description 分页查询实验报告
     * @date 2023/7/31 11:49
     */
    public Page<ExptReportPageResponse> pageExptReport(ExptReportPageRequest pageRequest, String accessAccountId) {
        Integer sortByExptNameAsc = pageRequest.getSortByExptNameAsc();
        Integer sortByAllotTimeAsc = pageRequest.getSortByAllotTimeAsc();
        Integer sortByStartTimeAsc = pageRequest.getSortByStartTimeAsc();
        Integer sortByEndTimeAsc = pageRequest.getSortByEndTimeAsc();
        Integer sortByAllotUserNameAsc = pageRequest.getSortByAllotUserNameAsc();
        Integer sortByExptModeAsc = pageRequest.getSortByExptModeAsc();

        boolean isAdmin = baseBiz.isAdministrator(accessAccountId);
        Page<ExperimentInstanceEntity> pageResult = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getState, EnumExperimentState.FINISH.getState())
                .and(!isAdmin, wrapper -> {
                    // 教师可以看到自己的以及管理员已经发布的 todo @实验列表 exptInstance 提供个区分管理员和教师端的
                    wrapper.eq(ExperimentInstanceEntity::getAccountId, accessAccountId);
                })
                .like(StrUtil.isNotBlank(pageRequest.getKeyword()), ExperimentInstanceEntity::getExperimentName, pageRequest.getKeyword())
//                .orderBy()
                .page(pageRequest.getPage());
        return convertPageResult(pageResult);
    }

    /**
     * @param pageRequest - 实验小组报告分页请求
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptGroupReportPageResponse>
     * @author fhb
     * @description 分页请求小组报告
     * @date 2023/7/31 14:16
     */
    public Page<ExptGroupReportPageResponse> pageGroupReport(ExptGroupReportPageRequest pageRequest) {
        Page<ExperimentRankingEntity> pageResult = experimentRankingService.lambdaQuery()
                .eq(ExperimentRankingEntity::getExperimentInstanceId, pageRequest.getExptInstanceId())
                .page(pageRequest.getPage());
        return convertGroupPageResult(pageResult);
    }

    /**
     * @param pageRequest - 个人查询小组报告请求
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.request.ExptAccountReportRequest>
     * @author fhb
     * @description  个人查询小组报告
     * @date 2023/7/31 15:44
     */
    public Page<ExptAccountReportResponse> pageAccountReport(ExptAccountReportRequest pageRequest) {
        Page<ExperimentParticipatorEntity> pageResult = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getAccountId, pageRequest.getAccountId())
                .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                .page(pageRequest.getPage());
        return convertAccountPageResult(pageResult);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author - fhb
     * @description 获取实验报告
     * @date 2023/7/21 14:11
     */
    public ExptReportVO exportExptReport(String exptInstanceId) {
        // check
        ExperimentInstanceEntity exptEntity = checkExpt(exptInstanceId);
        String exptZipName = exptEntity.getId()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptEntity.getExperimentName()
                + SystemConstant.SUFFIX_ZIP;

        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + exptInstanceId);
        try {
            if (lock.tryLock(-1, 30, TimeUnit.SECONDS)) {
                // 查询是否已经存在
                String reportOfExpt = reportRecordHelper.getReportOfExpt(exptInstanceId, ExptReportTypeEnum.EXPT_ZIP);
                if (StrUtil.isNotBlank(reportOfExpt)) {
                    ExptReportVO exptReportVO = generatePdf(exptInstanceId, null);
                    exptReportVO.setZipName(exptZipName);
                    exptReportVO.setZipPath(reportOfExpt);
                    return exptReportVO;
                }

                // 生成报告
                ExptReportVO exptReportVO = generatePdf(exptInstanceId, null);
                exptReportVO.setZipName(exptZipName);
                reportZipHelper.zipAndUpload(exptReportVO);

                // 记录一份数据
                if (StrUtil.isNotBlank(exptReportVO.getZipPath())) {
                    MaterialsAttachmentRequest attachment = MaterialsAttachmentRequest.builder()
                            .fileName(exptReportVO.getZipName())
                            .fileType("zip")
                            .fileUri(exptReportVO.getZipPath())
                            .build();
                    MaterialsRequest materialsRequest = MaterialsRequest.builder()
                            .bizCode("EXPT")
                            .title(exptReportVO.getZipName())
                            .materialsAttachments(List.of(attachment))
                            .build();
                    reportRecordHelper.record(exptInstanceId, null, ExptReportTypeEnum.EXPT_ZIP, materialsRequest);
                }
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 获取小组实验报告
     * @date 2023/7/21 14:09
     */
    public ExptReportVO exportGroupReport(String exptInstanceId, String exptGroupId) {
        // check
        ExperimentInstanceEntity exptEntity = checkExpt(exptInstanceId);
        String groupZipName = exptEntity.getId()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptEntity.getExperimentName()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptGroupId
                + SystemConstant.SUFFIX_ZIP;

        RLock lock = redissonClient.getLock(RedisKeyConst.HM_LOCK_REPORT + exptGroupId);
        try {
            if (lock.tryLock(-1, 10, TimeUnit.SECONDS)) {
                // 查询是否已经存在
                String reportOfGroup = reportRecordHelper.getReportOfGroup(exptInstanceId, exptGroupId, ExptReportTypeEnum.GROUP_ZIP);
                if (StrUtil.isNotBlank(reportOfGroup)) {
                    ExptReportVO exptReportVO = generatePdf(exptInstanceId, exptGroupId);
                    exptReportVO.setZipName(groupZipName);
                    exptReportVO.setZipPath(reportOfGroup);
                    return exptReportVO;
                }

                // 生成报告
                ExptReportVO exptReportVO = generatePdf(exptInstanceId, exptGroupId);
                exptReportVO.setZipName(groupZipName);
                reportZipHelper.zipAndUpload(exptReportVO);

                // 记录一份数据
                if (StrUtil.isNotBlank(exptReportVO.getZipPath())) {
                    MaterialsAttachmentRequest attachment = MaterialsAttachmentRequest.builder()
                            .fileName(exptReportVO.getZipName())
                            .fileType("zip")
                            .fileUri(exptReportVO.getZipPath())
                            .build();
                    MaterialsRequest materialsRequest = MaterialsRequest.builder()
                            .bizCode("EXPT")
                            .title(exptReportVO.getZipName())
                            .materialsAttachments(List.of(attachment))
                            .build();
                    reportRecordHelper.record(exptInstanceId, exptGroupId, ExptReportTypeEnum.GROUP_ZIP, materialsRequest);
                }
                return exptReportVO;
            } else {
                throw new BizException("报告生成中");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

        return ExptReportVO.emptyVO();
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param accountId      - 账号ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 获取学生账号的实验报告
     * @date 2023/7/21 14:08
     */
    public ExptReportVO exportAccountReport(String exptInstanceId, String accountId) {
        String experimentGroupId = getGroupOfAccountAndExpt(exptInstanceId, accountId);

        return exportGroupReport(exptInstanceId, experimentGroupId);
    }

    public void previewExptReport(String exptInstanceId, HttpServletRequest request, HttpServletResponse response) {

    }

    public void previewGroupReport(String exptInstanceId, String exptGroupId, HttpServletRequest request, HttpServletResponse response) {

    }

    public void previewAccountReport(String exptInstanceId, String accountId, HttpServletRequest request, HttpServletResponse response) {

    }

    private void preview(String urlStr, HttpServletRequest request, HttpServletResponse response) throws IOException {
        URL url = URLUtil.url(urlStr);
        // 下载文件到临时目录
        Path tempFilePath = Files.createTempFile("temp-", null);
        try (InputStream in = url.openStream()) {
            Files.copy(in, tempFilePath, StandardCopyOption.REPLACE_EXISTING);
        }
        // 读取临时文件内容为字节数组
        byte[] fileData = FileUtils.readFileToByteArray(tempFilePath.toFile());
        // 设置响应的内容类型
        String contentType = request.getServletContext().getMimeType(tempFilePath.getFileName().toString());
        response.setContentType(contentType);
        // 将文件数据写入响应
        response.getOutputStream().write(fileData);
        // 删除临时文件
        Files.deleteIfExists(tempFilePath);
    }

    private ExperimentInstanceEntity checkExpt(String experimentInstanceId) {
        ExperimentInstanceEntity exptInstance = experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, experimentInstanceId)
                .oneOpt()
                .orElseThrow(() -> new BizException("实验不存在"));
        Integer state = exptInstance.getState();
        if (state < EnumExperimentState.FINISH.getState()) {
            throw new BizException("实验还未结束，请等待");
        }
        return exptInstance;
    }

    // todo 后续策略模式
    private ExptReportVO generatePdf(String experimentInstanceId, String experimentGroupId) {
        List<ExptGroupReportVO> exptGroupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(exptGroupReportVOS)
                .build();

        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(experimentInstanceId);
        switch (exptSettingMode) {
            case SCHEME -> {
                ExptReportVO schemeReportVO = schemeReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
            }
            case SAND -> {
                ExptReportVO sandReportVO = sandReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
            case SAND_SCHEME -> {
                ExptReportVO schemeReportVO = schemeReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId);
                ExptReportVO sandReportVO = sandReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
        }
        // 实验总报告
        ExptReportVO overviewReportVO = overviewReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId);
        exptGroupReportVOS.addAll(overviewReportVO.getGroupReportList());

        return result;
    }

    // 获取该实验,该账号的 小组ID
    private String getGroupOfAccountAndExpt(String exptInstanceId, String accountId) {
        ExperimentParticipatorEntity experimentParticipatorEntity = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .eq(ExperimentParticipatorEntity::getAccountId, accountId)
                .oneOpt()
                .orElseThrow(() -> new BizException("获取用户实验报告时, 获取实验参与者信息异常"));
        return experimentParticipatorEntity.getExperimentGroupId();
    }

    private Page<ExptReportPageResponse> convertPageResult(Page<ExperimentInstanceEntity> pageResult) {
        Page<ExptReportPageResponse> result = BeanUtil.copyProperties(pageResult, Page.class);
        List<ExperimentInstanceEntity> records = pageResult.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        List<ExptReportPageResponse> ts = new ArrayList<>();
        records.forEach(item -> {
            ExptReportPageResponse reportPageResponse = ExptReportPageResponse.builder()
                    .exptInstanceId(item.getExperimentInstanceId())
                    .exptName(item.getExperimentName())
                    .exptAllotTime(item.getDt())
                    .exptStartTime(item.getStartTime())
                    .exptEndTime(item.getEndTime())
                    // 没有提供实验班级的数据
//                    .clazzName()
                    .exptState(item.getState())
                    .exptStateName(EnumExperimentState.getNameByCode(item.getState()))
                    .allotUserName(item.getAppointorName())
                    .exptMode(EnumExperimentMode.getNameByCode(item.getModel()))
                    .build();
            ts.add(reportPageResponse);
        });

        result.setRecords(ts);
        return result;
    }

    private Page<ExptGroupReportPageResponse> convertGroupPageResult(Page<ExperimentRankingEntity> pageResult) {
        Page<ExptGroupReportPageResponse> result = BeanUtil.copyProperties(pageResult, Page.class);
        List<ExperimentRankingEntity> records = pageResult.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        List<ExptGroupReportPageResponse> ts = new ArrayList<>();
        // 获取所有小组信息
        List<String> exptGroupIds = records.stream()
                .map(ExperimentRankingEntity::getExperimentGroupId)
                .toList();
        Map<String, String> groupIdMapMember = groupIdMapAccountName(exptGroupIds);
        records.forEach(item -> {
            String exptGroupId = item.getExperimentGroupId();
            String member = groupIdMapMember.get(exptGroupId) == null ? "" : groupIdMapMember.get(exptGroupId);
            ExptGroupReportPageResponse reportPageResponse = ExptGroupReportPageResponse.builder()
                    .exptGroupId(item.getExperimentGroupId())
                    .exptGroupName(item.getGroupName())
                    .exptGroupAlign(item.getGroupAlias())
                    .exptGroupMembers(member)
                    .totalScore(item.getTotalScore())
                    .build();
            ts.add(reportPageResponse);
        });

        result.setRecords(ts);
        return result;
    }

    private Page<ExptAccountReportResponse> convertAccountPageResult(Page<ExperimentParticipatorEntity> pageResult) {
        Page<ExptAccountReportResponse> result = BeanUtil.copyProperties(pageResult, Page.class);
        List<ExperimentParticipatorEntity> records = pageResult.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        List<ExptAccountReportResponse> ts = new ArrayList<>();
        records.forEach(item -> {
            ExptAccountReportResponse itemResponse = ExptAccountReportResponse.builder()
                    .exptName(item.getExperimentName())
                    .exptMode(EnumExperimentMode.getNameByCode(item.getModel()))
                    .exptStartTime(item.getExperimentStartTime())
                    .exptEndTime(item.getExperimentEndTime())
                    .build();
            ts.add(itemResponse);
        });

        result.setRecords(ts);
        return result;
    }

    // todo @uim 提供批量方法
    private Map<String, String> groupIdMapAccountName(List<String> exptGroupIds) {
        Map<String, String> result = new HashMap<>();

        // 获取所有参与者信息
        List<ExperimentParticipatorEntity> patorList = experimentParticipatorService.lambdaQuery()
                .in(ExperimentParticipatorEntity::getExperimentGroupId, exptGroupIds)
                .list();
        Assert.notEmpty(patorList, "获取实验小组报告时，获取实验参与者信息不能为空");
        Map<String, List<ExperimentParticipatorEntity>> groupCollect = patorList.stream()
                .collect(Collectors.groupingBy(ExperimentParticipatorEntity::getExperimentGroupId));

        // 创建小组和组员的映射 todo @uim 提供批量
        groupCollect.forEach((groupId, v) -> {
            String member = v.stream()
                    .map(item -> baseBiz.getUserName(item.getAccountId()))
                    .collect(Collectors.joining(","));
            result.put(groupId, member);
        });

        return result;
    }
}
