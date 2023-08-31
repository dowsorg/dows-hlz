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
import org.dows.hep.api.user.experiment.response.ExptSchemeScoreRankResponse;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentRankingEntity;
import org.dows.hep.service.ExperimentGroupService;
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
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final ExperimentGroupService experimentGroupService;

    private final ExptSchemeReportHandler schemeReportHandler;
    private final ExptSandReportHandler sandReportHandler;
    private final ExptOverviewReportHandler overviewReportHandler;

    private final ReportZipHelper reportZipHelper;
    private final ReportRecordHelper reportRecordHelper;

    /**
     * @param pageRequest     - 分页实验报告请求
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
                    wrapper.eq(ExperimentInstanceEntity::getAccountId, accessAccountId)
                            .or()
                            .eq(ExperimentInstanceEntity::getAppointor, "Admin");
                })
                .like(StrUtil.isNotBlank(pageRequest.getKeyword()), ExperimentInstanceEntity::getExperimentName, pageRequest.getKeyword())
                // 升序
//                .orderByAsc(sortByExptNameAsc != null && sortByExptNameAsc == 1, ExperimentInstanceEntity::getExperimentName)
//                .orderByAsc(sortByAllotTimeAsc != null && sortByAllotTimeAsc == 1, ExperimentInstanceEntity::getDt)
//                .orderByAsc(sortByStartTimeAsc != null && sortByStartTimeAsc == 1, ExperimentInstanceEntity::getStartTime)
//                .orderByAsc(sortByEndTimeAsc != null && sortByEndTimeAsc == 1, ExperimentInstanceEntity::getEndTime)
//                .orderByAsc(sortByAllotUserNameAsc != null && sortByAllotUserNameAsc == 1, ExperimentInstanceEntity::getAppointorName)
//                .orderByAsc(sortByExptModeAsc != null && sortByExptModeAsc == 1, ExperimentInstanceEntity::getModel)
                // 降序
//                .orderByDesc(sortByExptNameAsc != null && sortByExptNameAsc == 0, ExperimentInstanceEntity::getExperimentName)
//                .orderByDesc(sortByAllotTimeAsc != null && sortByAllotTimeAsc == 0, ExperimentInstanceEntity::getDt)
//                .orderByDesc(sortByStartTimeAsc != null && sortByStartTimeAsc == 0, ExperimentInstanceEntity::getStartTime)
//                .orderByDesc(sortByEndTimeAsc != null && sortByEndTimeAsc == 0, ExperimentInstanceEntity::getEndTime)
//                .orderByDesc(sortByAllotUserNameAsc != null && sortByAllotUserNameAsc == 0, ExperimentInstanceEntity::getAppointorName)
//                .orderByDesc(sortByExptModeAsc != null && sortByExptModeAsc == 0, ExperimentInstanceEntity::getModel)
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
        Page<ExperimentGroupEntity> pageResult = experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, pageRequest.getExptInstanceId())
                .page(pageRequest.getPage());
        return convertGroupPageResult(pageResult, pageRequest.getExptInstanceId());
    }

    /**
     * @param pageRequest - 个人查询小组报告请求
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.request.ExptAccountReportRequest>
     * @author fhb
     * @description 个人查询小组报告
     * @date 2023/7/31 15:44
     */
    public Page<ExptAccountReportResponse> pageAccountReport(ExptAccountReportRequest pageRequest) {
        Page<ExperimentParticipatorEntity> pageResult = experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getAccountId, pageRequest.getAccountId())
                .eq(ExperimentParticipatorEntity::getState, EnumExperimentState.FINISH.getState())
                .orderByDesc(ExperimentParticipatorEntity::getExperimentStartTime)
                .page(pageRequest.getPage());
        return convertAccountPageResult(pageResult);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param regenerate     - 是否需要重新生成
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author - fhb
     * @description 获取实验报告
     * @date 2023/7/21 14:11
     */
    public ExptReportVO exportExptReport(String exptInstanceId, boolean regenerate) {
        // check
        ExperimentInstanceEntity exptEntity = checkExpt(exptInstanceId);
        String exptZipName = exptEntity.getId()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptEntity.getExperimentName()
                + SystemConstant.SUFFIX_ZIP;

        RLock lock = redissonClient.getLock(RedisKeyConst.HEP_LOCK_REPORT + exptInstanceId);
        try {
            if (lock.tryLock(-1, 30, TimeUnit.SECONDS)) {
                // 查询是否已经存在
                String reportOfExpt = reportRecordHelper.getReportOfExpt(exptInstanceId, ExptReportTypeEnum.EXPT_ZIP);

                /*1.使用旧数据*/
                // 不重新生成并且旧数据存在 --> 直接返回
                if (!regenerate && StrUtil.isNotBlank(reportOfExpt)) {
                    ExptReportVO exptReportVO = generatePdf(exptInstanceId, null, false);
                    exptReportVO.setZipName(exptZipName);
                    exptReportVO.setZipPath(reportOfExpt);
                    return exptReportVO;
                }

                /*2.使用新数据*/
                // 生成报告
                ExptReportVO exptReportVO = generatePdf(exptInstanceId, null, true);
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
     * @param regenerate     - 是否重新生成
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 获取小组实验报告
     * @date 2023/7/21 14:09
     */
    public ExptReportVO exportGroupReport(String exptInstanceId, String exptGroupId, boolean regenerate) {
        // check
        ExperimentInstanceEntity exptEntity = checkExpt(exptInstanceId);
        String groupZipName = exptEntity.getId()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptEntity.getExperimentName()
                + SystemConstant.SPLIT_UNDER_LINE
                + exptGroupId
                + SystemConstant.SUFFIX_ZIP;

        RLock lock = redissonClient.getLock(RedisKeyConst.HEP_LOCK_REPORT + exptGroupId);
        try {
            if (lock.tryLock(-1, 10, TimeUnit.SECONDS)) {
                // 查询是否已经存在
                String reportOfGroup = reportRecordHelper.getReportOfGroup(exptInstanceId, exptGroupId, ExptReportTypeEnum.GROUP_ZIP);

                /*1.使用旧数据*/
                // 不重新生成并且旧数据存在 --> 直接返回
                if (!regenerate && StrUtil.isNotBlank(reportOfGroup)) {
                    ExptReportVO exptReportVO = generatePdf(exptInstanceId, exptGroupId, false);
                    exptReportVO.setZipName(groupZipName);
                    exptReportVO.setZipPath(reportOfGroup);
                    return exptReportVO;
                }

                /*2.使用新数据*/
                // 生成报告
                ExptReportVO exptReportVO = generatePdf(exptInstanceId, exptGroupId, true);
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
    public ExptReportVO exportAccountReport(String exptInstanceId, String accountId, boolean regenerate) {
        String experimentGroupId = getGroupOfAccountAndExpt(exptInstanceId, accountId);

        return exportGroupReport(exptInstanceId, experimentGroupId, regenerate);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param regenerate     - 是否重新生成
     * @param request        - http 请求
     * @param response       - http 响应
     * @author fhb
     * @description 预览实验报告
     * @date 2023/8/24 17:41
     */
    public void previewExptReport(String exptInstanceId, boolean regenerate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExptReportVO exptReportVO = exportExptReport(exptInstanceId, regenerate);
        List<ExptGroupReportVO> groupReportList = exptReportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return;
        }

        // 获取实验总报告
        ExptGroupReportVO.ReportFile reportFile = groupReportList.stream()
                .filter(item -> StrUtil.isBlank(item.getExptGroupId()))
                .findFirst()
                .map(item -> item.getPaths().get(0))
                .orElse(null);
        if (null == reportFile) {
            return;
        }

        String path = reportFile.getPath();
        preview(path, request, response);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @param regenerate     - 是否重新生成
     * @param request        - Http 请求
     * @param response       - Http 响应
     * @author fhb
     * @description 预览小组报告
     * @date 2023/8/24 17:42
     */
    public void previewGroupReport(String exptInstanceId, String exptGroupId, boolean regenerate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ExptReportVO exptReportVO = exportGroupReport(exptInstanceId, exptGroupId, regenerate);
        List<ExptGroupReportVO> groupReportList = exptReportVO.getGroupReportList();
        if (CollUtil.isEmpty(groupReportList)) {
            return;
        }

        // 获取实验总报告
        ExptGroupReportVO.ReportFile reportFile = groupReportList.stream()
                .filter(item -> item.getExptGroupId().equals(exptGroupId))
                .findFirst()
                .map(item -> item.getPaths().get(0))
                .orElse(null);
        if (null == reportFile) {
            return;
        }

        String path = reportFile.getPath();
        preview(path, request, response);
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param accountId      - 账号ID
     * @param regenerate     - 是否重新生成
     * @param request        - Http 请求
     * @param response       - Http 响应
     * @author fhb
     * @description 预览学生报告
     * @date 2023/8/24 17:43
     */
    public void previewAccountReport(String exptInstanceId, String accountId, boolean regenerate, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String experimentGroupId = getGroupOfAccountAndExpt(exptInstanceId, accountId);
        previewGroupReport(exptInstanceId, experimentGroupId, regenerate, request, response);
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
        response.setContentType("application/pdf");
       /* String contentType = request.getServletContext().getMimeType(tempFilePath.getFileName().toString());
        response.setContentType(contentType);*/

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
    private ExptReportVO generatePdf(String experimentInstanceId, String experimentGroupId, boolean regenerate) {
        List<ExptGroupReportVO> exptGroupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(exptGroupReportVOS)
                .build();

        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(experimentInstanceId);
        switch (exptSettingMode) {
            case SCHEME -> {
                ExptReportVO schemeReportVO = schemeReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId, regenerate);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
            }
            case SAND -> {
                ExptReportVO sandReportVO = sandReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId, regenerate);
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
            case SAND_SCHEME -> {
                ExptReportVO schemeReportVO = schemeReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId, regenerate);
                ExptReportVO sandReportVO = sandReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId, regenerate);
                exptGroupReportVOS.addAll(schemeReportVO.getGroupReportList());
                exptGroupReportVOS.addAll(sandReportVO.getGroupReportList());
            }
        }
        // 实验总报告
        ExptReportVO overviewReportVO = overviewReportHandler.generatePdfReport(experimentInstanceId, experimentGroupId, regenerate);
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

    private Page<ExptGroupReportPageResponse> convertGroupPageResult(Page<ExperimentGroupEntity> pageResult, String exptInstanceId) {
        Page<ExptGroupReportPageResponse> result = BeanUtil.copyProperties(pageResult, Page.class);
        List<ExperimentGroupEntity> records = pageResult.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }

        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(exptInstanceId);
        if (exptSettingMode == null) {
            throw new BizException("获取小组排行榜信息时：实验设置信息异常");
        }

        HashMap<String, String> scoreMap = new HashMap<>();
        switch (exptSettingMode) {
            case SAND -> {
                List<ExperimentRankingEntity> sandRankingList = experimentRankingService.lambdaQuery()
                        .eq(ExperimentRankingEntity::getExperimentInstanceId, exptInstanceId)
                        .list();
                if (CollUtil.isNotEmpty(sandRankingList)) {
                    sandRankingList.forEach(sand -> {
                        String groupId = sand.getExperimentGroupId();
                        String score = sand.getTotalScore();
                        scoreMap.put(groupId, score);
                    });
                }
            }
            case SCHEME -> {
                List<ExptSchemeScoreRankResponse> schemeRankingList = experimentSchemeBiz.listExptSchemeScoreRank(exptInstanceId);
                if (CollUtil.isNotEmpty(schemeRankingList)) {
                    schemeRankingList.forEach(scheme -> {
                        String groupId = scheme.getGroupId();
                        String score = scheme.getScore();
                        scoreMap.put(groupId, score);
                    });
                }
            }
        }

        List<ExptGroupReportPageResponse> ts = new ArrayList<>();
        // 获取所有小组信息
        List<String> exptGroupIds = records.stream()
                .map(ExperimentGroupEntity::getExperimentGroupId)
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
                    .totalScore(scoreMap.get(item.getExperimentGroupId()) == null ? "0" : scoreMap.get(item.getExperimentGroupId()))
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
                    .exptInstanceId(item.getExperimentInstanceId())
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

    private boolean regenerate(String experimentInstanceId) {
        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(experimentInstanceId);
        if (exptSettingMode == null) {
            throw new BizException(String.format("导出实验pdf报告时：获取实验%s的设置数据异常", experimentInstanceId));
        }
        boolean regenerate = Boolean.TRUE;
        if (ExptSettingModeEnum.SAND.equals(exptSettingMode)) {
            regenerate = Boolean.FALSE;
        }
        return regenerate;
    }
}
