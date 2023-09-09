package org.dows.hep.biz.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.commons.utils.Base64;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.api.base.indicator.response.*;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.user.experiment.ExptReportTypeEnum;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.api.user.experiment.response.ExptSchemeScoreRankResponse;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.properties.FindSoftProperties;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.vo.report.ExptBaseInfoModel;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptOverviewReportModel;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `报告总分` biz
 * @date 2023/7/7 10:20
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ExptOverviewReportHandler implements ExptReportHandler<ExptOverviewReportHandler.ExptOverviewReportData, ExptOverviewReportModel> {
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final ExperimentScoringBiz experimentScoringBiz;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentInstanceService experimentInstanceService;

    private final ReportOSSHelper ossHelper;
    private final SchemeReportPdfHelper schemeReportPdfHelper;
    private final SandReportPdfHelper sandReportPdfHelper;
    private final ReportRecordHelper recordHelper;
    private final FindSoftProperties findSoftProperties;

    private static final String LOCAL_OVERVIEW_REPORT = SystemConstant.PDF_REPORT_TMP_PATH + "实验总报告" + File.separator;

    @Data
    @Builder
    public static class ExptOverviewReportData implements ExptReportData {
        private ExperimentInstanceEntity exptInfo;
        private ExperimentRankResponse sandRank;
        private List<ExptSchemeScoreRankResponse> schemeRankList;
        private ExptSettingModeEnum settingModeEnum;
    }

    @Override
    public ExptReportVO generatePdfReport(String exptInstanceId, String exptGroupId, boolean regenerate) {
        // pdf 素材
        ExptOverviewReportData exptData = prepareData(exptInstanceId, exptGroupId);
        ExptOverviewReportModel pdfVO = convertData2Model(exptGroupId, exptData);
        String schemeFlt = getSchemeFlt();
        String fileName = getOutputPosition(exptGroupId, exptData);

        // 判断记录中是否有数据
        String reportOfGroup = recordHelper.getReportOfExpt(exptInstanceId, ExptReportTypeEnum.EXPT);

        /*1.使用旧数据*/
        // 不重新生成并且旧数据存在 --> 直接返回
        if (!regenerate && StrUtil.isNotBlank(reportOfGroup)) {
            ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                    .parent(exptInstanceId)
                    .name(fileName)
                    .path(reportOfGroup)
                    .build();
            ExptGroupReportVO groupReportVO = ExptGroupReportVO.builder()
                    .exptGroupId(null)
                    .exptGroupNo(null)
                    .reportFiles(List.of(reportFile))
                    .build();
            return ExptReportVO.builder()
                    .groupReportList(List.of(groupReportVO))
                    .build();
        }

        // 根据实验模式不同,准备不同数据
        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(exptInstanceId);

        /*2.使用新数据*/
        // 生成 pdf 并上传文件
        Path path = Paths.get(LOCAL_OVERVIEW_REPORT, fileName);
        Path uploadPath = Paths.get(exptInstanceId, fileName);
        OssInfo ossInfo = null;
        String fileUri = "";
        if (ExptSettingModeEnum.SCHEME.equals(exptSettingMode)) {
            ossInfo = schemeReportPdfHelper.convertAndUpload(pdfVO, schemeFlt, path, uploadPath);
            fileUri = ossHelper.getUrlPath(ossInfo, exptInstanceId);
        }
        if (ExptSettingModeEnum.SAND.equals(exptSettingMode)) {
            ossInfo = sandReportPdfHelper.convertAndUpload(exptInstanceId, exptGroupId, uploadPath);
            fileUri = ossInfo.getPath();
        }

        // 记录一份数据
        assert ossInfo != null;
        if (StrUtil.isNotBlank(ossInfo.getPath())) {
            MaterialsAttachmentRequest attachment = MaterialsAttachmentRequest.builder()
                    .fileName(fileName)
                    .fileType("pdf")
                    .fileUri(fileUri)
                    .build();
            MaterialsRequest materialsRequest = MaterialsRequest.builder()
                    .bizCode("EXPT")
                    .title(fileName)
                    .materialsAttachments(List.of(attachment))
                    .build();
            recordHelper.record(exptInstanceId, null, ExptReportTypeEnum.EXPT, materialsRequest);
        }

        // build result
        ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                .parent(exptInstanceId)
                .name(ossInfo.getName())
                .path(fileUri)
                .build();
        ExptGroupReportVO groupReportVO = ExptGroupReportVO.builder()
                .exptGroupId(null)
                .exptGroupNo(null)
                .reportFiles(List.of(reportFile))
                .build();
        return ExptReportVO.builder()
                .groupReportList(List.of(groupReportVO))
                .build();
    }

    @Override
    public ExptOverviewReportData prepareData(String exptInstanceId, String exptGroupId) {
        // 实验信息
        ExperimentInstanceEntity exptInfo = getExptInfo(experimentInstanceService, exptInstanceId);
        // 根据实验模式不同,准备不同数据
        ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(exptInstanceId);
        List<ExptSchemeScoreRankResponse> schemeRankList = null;
        ExperimentRankResponse sandRank = null;
        if (ExptSettingModeEnum.SCHEME.equals(exptSettingMode)) {
            schemeRankList = experimentSchemeBiz.listExptSchemeScoreRank(exptInstanceId);
        }
        if (ExptSettingModeEnum.SAND.equals(exptSettingMode)) {
            sandRank = getRank(exptInstanceId);
        }

        return ExptOverviewReportData.builder()
                .settingModeEnum(exptSettingMode)
                .exptInfo(exptInfo)
                .sandRank(sandRank)
                .schemeRankList(schemeRankList)
                .build();
    }

    @Override
    public ExptOverviewReportModel convertData2Model(String exptGroupId, ExptOverviewReportData exptReportData) {
        // 基本信息
        ExptBaseInfoModel baseInfo = generateBaseInfoVO(findSoftProperties);
        // 实验信息
        ExperimentInstanceEntity exptInfo1 = exptReportData.getExptInfo();
        ExptOverviewReportModel.ExptInfo exptInfo = ExptOverviewReportModel.ExptInfo.builder()
                .experimentName(exptInfo1.getExperimentName())
                .exptStartDate(exptInfo1.getStartTime() == null ? "" : DateUtil.formatDate(exptInfo1.getStartTime()))
                .build();
        // 根据实验模式不同,准备不同数据
        List<ExptOverviewReportModel.SchemeRanking> schemeRankingList = null;
        List<ExptOverviewReportModel.SandGroupRanking> sandGroupRankingList = null;
        List<List<ExptOverviewReportModel.SandPeriodRanking>> sandPeriodRankingList = null;
        List<ExptOverviewReportModel.TotalRanking> totalRankingList = null;
        ExptSettingModeEnum settingModeEnum = exptReportData.getSettingModeEnum();
        if (ExptSettingModeEnum.SCHEME.equals(settingModeEnum)) {
            schemeRankingList = generateSchemeRanking(exptReportData);
        } else if (ExptSettingModeEnum.SAND.equals(settingModeEnum)) {
            sandGroupRankingList = generateSandGroupRanking(exptReportData);
            sandPeriodRankingList = generateSandPeriodRanking(exptReportData);
        } else if (ExptSettingModeEnum.SAND_SCHEME.equals(settingModeEnum)) {
            schemeRankingList = generateSchemeRanking(exptReportData);
            sandGroupRankingList = generateSandGroupRanking(exptReportData);
            sandPeriodRankingList = generateSandPeriodRanking(exptReportData);
            totalRankingList = generateTotalRanking(schemeRankingList, sandGroupRankingList);
        }

        return ExptOverviewReportModel.builder()
                .baseInfo(baseInfo)
                .exptInfo(exptInfo)
                .totalRankingList(totalRankingList)
                .schemeRankingList(schemeRankingList)
                .sandGroupRankingList(sandGroupRankingList)
                .sandPeriodRankingList(sandPeriodRankingList)
                .build();
    }

    @Override
    public String getOutputPosition(String exptGroupId, ExptOverviewReportData exptReportData) {
        ExperimentInstanceEntity exptInfo = exptReportData.getExptInfo();
        // 文件名
        return exptInfo.getExperimentName()
                + SystemConstant.SPLIT_UNDER_LINE
                + "总报告"
                + SystemConstant.SUFFIX_PDF;
    }

    @Override
    public String getSchemeFlt() {
        return findSoftProperties.getExptOverviewFtl();
    }

    private ExperimentRankResponse getRank(String exptInstanceId) {
        ExperimentRankResponse result = null;
        try {
            result = experimentScoringBiz.getRank(exptInstanceId);
        } catch (Exception e) {
            log.error("获取总报告时，获取实验 {} 的排行榜数据异常", exptInstanceId);
            throw new BizException(String.format("获取总报告时，获取实验 %s 的排行榜数据异常", exptInstanceId));
        }
        return result;
    }

    private ExptBaseInfoModel generateBaseInfoVO(FindSoftProperties findSoftProperties) {
        String logoStr = null;
        String coverStr = null;
        try {
            logoStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getLogo()).getInputStream()));
            coverStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getCover()).getInputStream()));
        } catch (IOException e) {
            log.error("导出实验报告时，获取logo和cover图片资源异常");
            throw new BizException("导出实验报告时，获取logo和cover图片资源异常");
        }

        return ExptBaseInfoModel.builder()
                .title(findSoftProperties.getExptOverviewReportTitle())
                .logoImg(logoStr)
                .coverImg(coverStr)
                .copyRight(findSoftProperties.getCopyRight())
                .build();
    }

    private List<ExptOverviewReportModel.TotalRanking> generateTotalRanking(List<ExptOverviewReportModel.SchemeRanking> schemeRankingList, List<ExptOverviewReportModel.SandGroupRanking> sandGroupRankingList) {
        List<ExptOverviewReportModel.TotalRanking> result = new ArrayList<>();
        Map<String, String> collect = sandGroupRankingList.stream().collect(Collectors.toMap(ExptOverviewReportModel.SandGroupRanking::getGroupNo, ExptOverviewReportModel.SandGroupRanking::getGroupScore));

        for (ExptOverviewReportModel.SchemeRanking schemeRanking : schemeRankingList) {
            String schemeScore = schemeRanking.getSchemeScore();
            String sandScore = collect.get(schemeRanking.getGroupNo());
            BigDecimal bSchemeScore = new BigDecimal(schemeScore);
            BigDecimal bSandScore = new BigDecimal(sandScore);
            BigDecimal total = bSchemeScore.add(bSandScore);
            BigDecimal totalScore = total.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);

            ExptOverviewReportModel.TotalRanking totalRanking = ExptOverviewReportModel.TotalRanking.builder()
                    .groupNo(schemeRanking.getGroupNo())
                    .groupName(schemeRanking.getGroupName())
                    .schemeScore(schemeScore)
                    .sandScore(sandScore)
                    .totalScore(totalScore.toString())
                    .build();
            result.add(totalRanking);
        }

        if (CollUtil.isNotEmpty(result)) {
            result = result.stream().sorted((v1, v2) -> {
                String totalScore1 = v1.getTotalScore();
                String totalScore2 = v2.getTotalScore();

                BigDecimal b1 = new BigDecimal(totalScore1);
                BigDecimal b2 = new BigDecimal(totalScore2);
                return b1.compareTo(b2);
            }).toList();
        }
        return result;
    }

    private List<List<ExptOverviewReportModel.SandPeriodRanking>> generateSandPeriodRanking(ExptOverviewReportData exptReportData) {
        ExperimentRankResponse sandRank = exptReportData.getSandRank();
        if (BeanUtil.isEmpty(sandRank)) {
            return new ArrayList<>();
        }
        List<ExperimentRankItemResponse> periodRankItem = sandRank.getExperimentRankItemResponseList();
        if (CollUtil.isEmpty(periodRankItem)) {
            return new ArrayList<>();
        }

        List<List<ExptOverviewReportModel.SandPeriodRanking>> result = new ArrayList<>();
        Integer totalPeriod = sandRank.getTotalPeriod();
        Map<Integer, ExperimentRankItemResponse> periodMapEntity = periodRankItem.stream()
                .collect(Collectors.toMap(ExperimentRankItemResponse::getPeriods, item -> item));
        for (int i = 0; i < totalPeriod; i++) {
            // 获取该期对应的数据
            int period = i + 1;
            ExperimentRankItemResponse experimentRankItemResponse = periodMapEntity.get(period);
            List<ExperimentRankGroupItemResponse> groupItemList = experimentRankItemResponse.getExperimentRankGroupItemResponseList();

            // convert
            List<ExptOverviewReportModel.SandPeriodRanking> itemList = new ArrayList<>();
            for (ExperimentRankGroupItemResponse groupItem : groupItemList) {
                ExptOverviewReportModel.SandPeriodRanking resultItem = ExptOverviewReportModel.SandPeriodRanking.builder()
                        .groupNo(groupItem.getExperimentGroupName())
                        .groupName(groupItem.getExperimentGroupName())
                        .healthIndexScore(groupItem.getHealthIndexScore())
                        .knowledgeScore(groupItem.getKnowledgeScore())
                        .treatmentPercentScore(groupItem.getTreatmentPercentScore())
                        .totalScore(groupItem.getTotalScore())
                        .build();
                itemList.add(resultItem);
            }
            result.add(itemList);
        }

        return result;
    }

    private List<ExptOverviewReportModel.SandGroupRanking> generateSandGroupRanking(ExptOverviewReportData exptReportData) {
        ExperimentRankResponse sandRank = exptReportData.getSandRank();
        if (BeanUtil.isEmpty(sandRank)) {
            return new ArrayList<>();
        }
        List<ExperimentTotalRankItemResponse> totalRankItem = sandRank.getExperimentTotalRankItemResponseList();
        if (CollUtil.isEmpty(totalRankItem)) {
            return new ArrayList<>();
        }

        List<ExptOverviewReportModel.SandGroupRanking> result = new ArrayList<>();
        totalRankItem.forEach(item -> {
            // 小组每期的分数集合
            List<ExptOverviewReportModel.PeriodGroupScore> periodGroupScoreList = new ArrayList<>();
            List<ExperimentTotalRankGroupItemResponse> itemItemList = item.getExperimentTotalRankGroupItemResponseList();
            if (CollUtil.isNotEmpty(itemItemList)) {
                itemItemList.forEach(itemItem -> {
                    ExptOverviewReportModel.PeriodGroupScore periodGroupScore = ExptOverviewReportModel.PeriodGroupScore.builder()
                            .period(itemItem.getPeriods())
                            .score(itemItem.getTotalScore())
                            .build();
                    periodGroupScoreList.add(periodGroupScore);
                });
            }

            // 构建沙盘对抗排行榜
            ExptOverviewReportModel.SandGroupRanking sandGroupRanking = ExptOverviewReportModel.SandGroupRanking.builder()
                    .groupNo(item.getExperimentGroupNo())
                    .groupName(item.getExperimentGroupName())
                    .periodGroupScoreList(periodGroupScoreList)
                    .groupScore(item.getAllPeriodsTotalScore())
                    .build();
            result.add(sandGroupRanking);
        });
        return result;
    }

    private List<ExptOverviewReportModel.SchemeRanking> generateSchemeRanking(ExptOverviewReportData exptReportData) {
        List<ExptSchemeScoreRankResponse> schemeRankList = exptReportData.getSchemeRankList();
        if (CollUtil.isEmpty(schemeRankList)) {
            return new ArrayList<>();
        }

        List<ExptOverviewReportModel.SchemeRanking> result = new ArrayList<>();
        schemeRankList.forEach(scheme -> {
            ExptOverviewReportModel.SchemeRanking schemeRanking = ExptOverviewReportModel.SchemeRanking.builder()
                    .groupNo(scheme.getGroupNo())
                    .groupName(scheme.getGroupName())
                    .schemeScore(scheme.getScore())
                    .build();
            result.add(schemeRanking);
        });
        return result;
    }
}
