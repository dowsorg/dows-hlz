package org.dows.hep.biz.report;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.itextpdf.commons.utils.Base64;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.framework.oss.api.OssInfo;
import org.dows.hep.api.base.indicator.response.ExperimentRankGroupItemResponse;
import org.dows.hep.api.base.indicator.response.ExperimentRankItemResponse;
import org.dows.hep.api.base.indicator.response.ExperimentRankResponse;
import org.dows.hep.api.base.indicator.response.ExperimentTotalRankItemResponse;
import org.dows.hep.api.base.materials.request.MaterialsAttachmentRequest;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.ExptReportTypeEnum;
import org.dows.hep.api.user.experiment.dto.ExptQuestionnaireOptionDTO;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.biz.base.indicator.ExperimentIndicatorViewSupportExamRsBiz;
import org.dows.hep.biz.risk.RiskBiz;
import org.dows.hep.biz.user.experiment.ExperimentOrgBiz;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentScoringBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.biz.util.ShareUtil;
import org.dows.hep.entity.*;
import org.dows.hep.properties.FindSoftProperties;
import org.dows.hep.service.*;
import org.dows.hep.vo.report.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @author fhb
 * @version 1.0
 * @description 实验 `沙盘报告` biz
 * @date 2023/7/7 10:21
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ExptSandReportHandler implements ExptReportHandler<ExptSandReportHandler.ExptSandReportData, ExptSandReportModel> {
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
    private final ExperimentScoringBiz experimentScoringBiz;
    private final ExperimentGroupService experimentGroupService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentScoringService experimentScoringService;
    private final ExperimentOrgService experimentOrgService;
    private final ExperimentPersonService experimentPersonService;

    private final RiskBiz riskBiz;

    private final ReportOSSHelper ossHelper;
    private final SchemeReportPdfHelper schemeReportPdfHelper;
    private final SandReportPdfHelper sandReportPdfHelper;
    private final ReportRecordHelper recordHelper;
    private final FindSoftProperties findSoftProperties;
    private final ExperimentOrgBiz experimentOrgBiz;

    private static final String LOCAL_SAND_REPORT = SystemConstant.PDF_REPORT_TMP_PATH + "沙盘模拟实验报告" + File.separator;

    @Data
    @Builder
    public static class ExptSandReportData implements ExptReportData {
        private ExperimentInstanceEntity exptInfo;
        private List<ExperimentGroupEntity> exptGroupInfoList;
        private List<ExperimentParticipatorEntity> exptMemberList;
        private List<ExperimentScoringEntity> exptScoringList;
        private ExperimentSetting.SandSetting sandSetting;
        private ExperimentRankResponse experimentRankResponse;
        private List<ExperimentQuestionnaireResponse> exptQuestionnaireList;
        private List<ExperimentOrgEntity> exptOrgList;
        private List<ExperimentPersonEntity> exptPersonList;
    }

    /**
     * @param experimentInstanceId - 实验实例ID
     * @param exptGroupId          - 实验小组ID
     * @param regenerate
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 如果有 exptGroupId，则只导出该组的报告，否则导出所有组的报告
     * @date 2023/7/17 9:23
     */
    @Override
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId, boolean regenerate) {
        // 构建 result
        List<ExptGroupReportVO> groupReportVOs = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(groupReportVOs)
                .build();

        // 有小组ID则生成该小组的报告，
        // 没有小组ID则批量生成实验所有小组的报告
        ExptSandReportData exptData = prepareData(experimentInstanceId, exptGroupId);
        List<ExperimentGroupEntity> exptGroupInfoList = exptData.getExptGroupInfoList();
        if (StrUtil.isBlank(exptGroupId)) { // 批量-所有小组
            for (ExperimentGroupEntity group : exptGroupInfoList) {
                ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(experimentInstanceId, group.getExperimentGroupId(), exptData, regenerate);
                groupReportVOs.add(exptGroupReportVO);
            }
        } else { // 单个小组
            ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(experimentInstanceId, exptGroupId, exptData, regenerate);
            groupReportVOs.add(exptGroupReportVO);
        }

        return result;
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId    - 实验小组ID
     * @return org.dows.hep.biz.report.pdf.ExptSandReportBiz.ExptSandReportData
     * @author fhb
     * @description 预先准备好生成报告需要的数据
     * @date 2023/7/17 11:07
     */
    @Override
    public ExptSandReportData prepareData(String exptInstanceId, String exptGroupId) {
        // 准备`生成报告`所需的数据
        return ExptSandReportData.builder()
                .exptInfo(getExptInfo(experimentInstanceService, exptInstanceId))
                .exptGroupInfoList(listExptGroupInfo(experimentGroupService, exptInstanceId, exptGroupId))
                .exptMemberList(listExptMembers(experimentParticipatorService, exptInstanceId, exptGroupId))
                .exptScoringList(listExptScoring(exptInstanceId, exptGroupId))
                .sandSetting(getExptSandSetting(exptInstanceId))
                .experimentRankResponse(getRank(exptInstanceId))
                .exptQuestionnaireList(listExptQuestionnaire(exptInstanceId, exptGroupId))
                .exptOrgList(listExptOrg(exptInstanceId))
                .exptPersonList(listExptPerson(exptInstanceId, exptGroupId))
                .build();
    }

    /**
     * @param exptGroupId - 实验小组ID
     * @param exptData    - 生成 `填充模板数据model` 需要的数据支持
     * @return org.dows.hep.vo.report.ExptSandReportModel
     * @author fhb
     * @description 生成pdf所需要填充的数据
     * @date 2023/7/17 13:36
     */
    @Override
    public ExptSandReportModel convertData2Model(String exptGroupId, ExptSandReportData exptData) {
        ExptBaseInfoModel baseInfoVO = generateBaseInfoVO(findSoftProperties);
        ExptSandReportModel.GroupInfo groupInfo = generateGroupInfo(exptGroupId, exptData);
        ExptSandReportModel.ScoreInfo scoreInfo = generateScoreInfo(exptGroupId, exptData);
        List<ExptSandReportModel.NpcData> npcDataList = new LinkedList<>();
        try {
            npcDataList = generateNpcInfo(exptGroupId, exptData);
        } catch (Exception e) {
            log.error(String.format("获取npc数据异常，异常为%s", e));
        }
        List<List<ExptSandReportModel.KnowledgeAnswer>> periodQuestions = generatePeriodQuestionnaires(exptGroupId, exptData);

        return ExptSandReportModel.builder()
                .baseInfo(baseInfoVO)
                .groupInfo(groupInfo)
                .scoreInfo(scoreInfo)
                .npcDatas(npcDataList)
                .periodQuestions(periodQuestions)
                .build();
    }

    /**
     * @param exptGroupId    - 实验小组ID
     * @param exptReportData - 生成 `填充模板数据model` 需要的数据支持
     * @return java.io.File
     * @author fhb
     * @description pdf 生成的位置
     * @date 2023/7/17 13:37
     */
    @Override
    public String getOutputPosition(String exptGroupId, ExptSandReportData exptReportData) {
        ExperimentInstanceEntity exptInfo = exptReportData.getExptInfo();
        List<ExperimentGroupEntity> groupList = exptReportData.getExptGroupInfoList();
        if (CollUtil.isEmpty(groupList) || StrUtil.isBlank(exptGroupId)) {
            throw new BizException("获取沙盘模拟报告时，获取组员信息数据异常");
        }

        ExperimentGroupEntity groupEntity = groupList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .findFirst()
                .orElse(null);
        if (BeanUtil.isEmpty(groupEntity)) {
            throw new BizException("获取沙盘模拟报告时，获取组员信息数据异常");
        }

        // 文件名
        return "第" + groupEntity.getGroupNo() + "组"
                + SystemConstant.SPLIT_UNDER_LINE
                + exptInfo.getExperimentName()
                + SystemConstant.SPLIT_UNDER_LINE
                + "沙盘模拟报告"
                + SystemConstant.SUFFIX_PDF;
    }

    /**
     * @return java.lang.String
     * @author fhb
     * @description 获取pdf模板
     * @date 2023/7/17 13:37
     */
    @Override
    public String getSchemeFlt() {
        return findSoftProperties.getExptSandFtl();
    }

    // 生成 pdf 报告
    private ExptGroupReportVO generatePdfReportOfGroup(String exptInstanceId, String exptGroupId, ExptSandReportData exptData, boolean regenerate) {
        // pdf 素材
        ExptSandReportModel pdfVO = convertData2Model(exptGroupId, exptData);
        String schemeFlt = getSchemeFlt();
        String fileName = getOutputPosition(exptGroupId, exptData);

        // 判断记录中是否有数据
        String reportOfGroup = recordHelper.getReportOfGroup(exptInstanceId, exptGroupId, ExptReportTypeEnum.GROUP);

        /*1.使用旧数据*/
        // 不重新生成并且旧数据存在 --> 直接返回
        if (!regenerate && StrUtil.isNotBlank(reportOfGroup)) {
            ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                    .parent(exptInstanceId)
                    .name(fileName)
                    .path(reportOfGroup)
                    .build();
            return ExptGroupReportVO.builder()
                    .exptGroupId(exptGroupId)
                    .exptGroupNo(Integer.valueOf(pdfVO.getGroupInfo().getGroupNo()))
                    .reportFiles(List.of(reportFile))
                    .build();
        }


        /*2.使用新数据*/
        // 生成 pdf 并上传文件
        Path path = Paths.get(LOCAL_SAND_REPORT, fileName);
        Path uploadPath = Paths.get(exptInstanceId, fileName);
        OssInfo ossInfo = sandReportPdfHelper.convertAndUpload(exptInstanceId, exptGroupId, uploadPath);
        String fileUri = ossInfo.getPath();

        // 记录一份数据
        if (StrUtil.isNotBlank(ossInfo.getPath())) {
            CompletableFuture.runAsync(() -> {
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
                recordHelper.record(exptInstanceId, exptGroupId, ExptReportTypeEnum.GROUP, materialsRequest);
            });
        }

        // 构建返回信息
        ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                .parent(exptInstanceId)
                .name(ossInfo.getName())
                .path(fileUri)
                .build();
        return ExptGroupReportVO.builder()
                .exptGroupId(exptGroupId)
                .exptGroupNo(Integer.valueOf(pdfVO.getGroupInfo().getGroupNo()))
                .reportFiles(List.of(reportFile))
                .build();
    }

    private List<ExperimentScoringEntity> listExptScoring(String exptInstanceId, String exptGroupId) {
        return experimentScoringService.lambdaQuery()
                .eq(ExperimentScoringEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentScoringEntity::getExperimentGroupId, exptGroupId)
                .list();
    }

    private ExperimentSetting.SandSetting getExptSandSetting(String exptInstanceId) {
        return experimentSettingBiz.getSandSetting(exptInstanceId);
    }

    private ExperimentRankResponse getRank(String exptInstanceId) {
        ExperimentRankResponse result = null;
        try {
            result = experimentScoringBiz.getRank(exptInstanceId);
        } catch (Exception e) {
            log.error("获取沙盘模拟报告时，获取实验 {} 的排行榜数据异常", exptInstanceId);
            throw new BizException(String.format("获取沙盘模拟报告时，获取实验 %s 的排行榜数据异常", exptInstanceId));
        }
        return result;
    }

    private List<ExperimentQuestionnaireResponse> listExptQuestionnaire(String exptInstanceId, String exptGroupId) {
        return experimentQuestionnaireBiz.listExptQuestionnaire(exptInstanceId, exptGroupId, Boolean.TRUE);
    }

    private List<ExperimentOrgEntity> listExptOrg(String exptInstanceId) {
        return experimentOrgService.lambdaQuery()
                .eq(ExperimentOrgEntity::getExperimentInstanceId, exptInstanceId)
                .list();
    }

    private List<ExperimentPersonEntity> listExptPerson(String exptInstanceId, String exptGroupId) {
        return experimentPersonService.lambdaQuery()
                .eq(ExperimentPersonEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentPersonEntity::getExperimentGroupId, exptGroupId)
                .list();
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
                .title(findSoftProperties.getExptSandReportTitle())
                .logoImg(logoStr)
                .coverImg(coverStr)
                .copyRight(findSoftProperties.getCopyRight())
                .build();
    }

    private ExptSandReportModel.GroupInfo generateGroupInfo(String exptGroupId, ExptSandReportData exptData) {
        ExperimentInstanceEntity exptInfo = exptData.getExptInfo();
        List<ExperimentGroupEntity> groupList = exptData.getExptGroupInfoList();
        List<ExperimentParticipatorEntity> memberList = exptData.getExptMemberList();
        List<ExperimentOrgEntity> exptOrgList = exptData.getExptOrgList();
        List<ExperimentPersonEntity> exptPersonList = exptData.getExptPersonList();
        final ExptSandReportModel.GroupInfo emptyGroupInfo = new ExptSandReportModel.GroupInfo();

        if (CollUtil.isEmpty(groupList) || CollUtil.isEmpty(memberList)) {
            return emptyGroupInfo;
        }

        // filter group-info
        ExperimentGroupEntity groupEntity = groupList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .findFirst()
                .orElse(null);
        if (BeanUtil.isEmpty(groupEntity)) {
            return emptyGroupInfo;
        }

        // filter group-member
        List<String> groupMembers = memberList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .map(ExperimentParticipatorEntity::getAccountName)
                .toList();

        List<ExperimentPersonEntity> groupNPCPerson = exptPersonList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .toList();

        return ExptSandReportModel.GroupInfo.builder()
                .groupNo(groupEntity.getGroupNo())
                .groupName(groupEntity.getGroupName())
                .groupMembers(groupMembers)
                .caseName(exptInfo.getCaseName())
                .experimentName(exptInfo.getExperimentName())
                .exptStartDate(exptInfo.getStartTime() == null ? "" : DateUtil.formatDate(exptInfo.getStartTime()))
                .caseNum(CollUtil.isEmpty(groupNPCPerson) ? 0 : groupNPCPerson.size())
                .build();
    }

    private ExptSandReportModel.ScoreInfo generateScoreInfo(String exptGroupId, ExptSandReportData exptData) {
        List<ExperimentScoringEntity> exptScoringList = exptData.getExptScoringList();
        ExptSandReportModel.ScoreInfo emptyResult = getEmptyScoreInfo(exptGroupId, exptData);
        if (CollUtil.isEmpty(exptScoringList)) {
            return emptyResult;
        }

        // filter score-info
        List<ExperimentScoringEntity> groupScoreList = exptScoringList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .toList();
        if (CollUtil.isEmpty(groupScoreList)) {
            return emptyResult;
        }

        // 每期权重
        List<ExptSandReportModel.ScoreInfo.PeriodWeight> periodWeights = new ArrayList<>();
        ExperimentSetting.SandSetting sandSetting = exptData.getSandSetting();
        Integer periods = sandSetting.getPeriods();
        Map<String, Float> weightMap = sandSetting.getWeightMap();
        weightMap.forEach((period, weight) -> {
            ExptSandReportModel.ScoreInfo.PeriodWeight periodWeight = ExptSandReportModel.ScoreInfo.PeriodWeight.builder()
                    .periods(period)
                    .weight(String.valueOf(weight == null ? 0.00 : weight) + "%")
                    .build();
            periodWeights.add(periodWeight);
        });

        // 评分权重
        Float healthIndexWeight = sandSetting.getHealthIndexWeight();
        Float knowledgeWeight = sandSetting.getKnowledgeWeight();
        Float medicalRatioWeight = sandSetting.getMedicalRatioWeight();
        ExptSandReportModel.ScoreInfo.ScoreWeight scoreWeight = ExptSandReportModel.ScoreInfo.ScoreWeight.builder()
                .healthIndexWeight(String.valueOf(healthIndexWeight == null ? 0.00 : healthIndexWeight) + "%")
                .knowledgeWeight(String.valueOf(knowledgeWeight == null ? 0.00 : knowledgeWeight) + "%")
                .treatmentPercentWeight(String.valueOf(medicalRatioWeight == null ? 0.00 : medicalRatioWeight) + "%")
                .build();

        // 期数得分信息
        List<ExptSandReportModel.ScoreInfo.PeriodScore> periodScores = new ArrayList<>();
        Map<Integer, ExperimentScoringEntity> periodMapScore = groupScoreList.stream()
                .collect(Collectors.toMap(ExperimentScoringEntity::getPeriods, item -> item, (v1, v2) -> v1));
        Map<Integer, Integer> periodMapRank = getPeriodRank(exptGroupId, exptData);
        for (int i = 0; i < periods; i++) {
            Integer period = i + 1;
            ExperimentScoringEntity v = periodMapScore.get(period);
            if (BeanUtil.isEmpty(v)) {
                continue;
            }
            ExptSandReportModel.ScoreInfo.Score scoreInfo = ExptSandReportModel.ScoreInfo.Score.builder()
                    .healthIndexScore(v.getHealthIndexScore())
                    .knowledgeScore(v.getKnowledgeScore())
                    .treatmentPercentScore(v.getTreatmentPercentScore())
                    .totalScore(v.getTotalScore())
                    .totalRanking(String.valueOf(periodMapRank.get(period)))
                    .build();
            ExptSandReportModel.ScoreInfo.PeriodScore periodScore = ExptSandReportModel.ScoreInfo.PeriodScore.builder()
                    .periods(String.valueOf(i + 1))
                    .scoreInfo(scoreInfo)
                    .build();
            periodScores.add(periodScore);
        }

        // 总得分
        BigDecimal tHealthIndexScore = BigDecimal.ZERO;
        BigDecimal tKnowledgeScore = BigDecimal.ZERO;
        BigDecimal tTreatmentPercentScore = BigDecimal.ZERO;
        BigDecimal tTotalScore = BigDecimal.ZERO;
        for (int i = 0; i < periods; i++) {
            ExperimentScoringEntity exptScore = periodMapScore.get(i + 1);
            if (BeanUtil.isEmpty(exptScore)) {
                continue;
            }
            String healthIndexScore = exptScore.getHealthIndexScore();
            String knowledgeScore = exptScore.getKnowledgeScore();
            String treatmentPercentScore = exptScore.getTreatmentPercentScore();
            String totalScore = exptScore.getTotalScore();
            // convert bigDecimal
            BigDecimal bHealthIndexScore = new BigDecimal(healthIndexScore);
            BigDecimal bKnowledgeScore = new BigDecimal(knowledgeScore);
            BigDecimal bTreatmentPercentScore = new BigDecimal(treatmentPercentScore);
            BigDecimal bTotalScore = new BigDecimal(totalScore);

            // weight
            Float periodWeight = weightMap.get(String.valueOf(i + 1));
            BigDecimal bPeriodWeight = BigDecimal.valueOf(periodWeight).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            // score mul weight
            BigDecimal pHealthIndexScore = bHealthIndexScore.multiply(bPeriodWeight);
            BigDecimal pKnowledgeScore = bKnowledgeScore.multiply(bPeriodWeight);
            BigDecimal pTreatmentPercentScore = bTreatmentPercentScore.multiply(bPeriodWeight);
            BigDecimal pTotalScore = bTotalScore.multiply(bPeriodWeight);

            // accumulation
            tHealthIndexScore = tHealthIndexScore.add(pHealthIndexScore);
            tKnowledgeScore = tKnowledgeScore.add(pKnowledgeScore);
            tTreatmentPercentScore = tTreatmentPercentScore.add(pTreatmentPercentScore);
            tTotalScore = tTotalScore.add(pTotalScore);
        }
        tHealthIndexScore = tHealthIndexScore.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        tKnowledgeScore = tKnowledgeScore.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        tTreatmentPercentScore = tTreatmentPercentScore.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        tTotalScore = tTotalScore.divide(BigDecimal.valueOf(periods), 2, RoundingMode.HALF_UP);
        Integer totalRank = getTotalRank(exptGroupId, exptData);
        ExptSandReportModel.ScoreInfo.Score totalScore = ExptSandReportModel.ScoreInfo.Score.builder()
                .healthIndexScore(tHealthIndexScore.toString())
                .knowledgeScore(tKnowledgeScore.toString())
                .treatmentPercentScore(tTreatmentPercentScore.toString())
                .totalScore(tTotalScore.toString())
                .totalRanking(String.valueOf(totalRank))
                .build();

        return ExptSandReportModel.ScoreInfo.builder()
                .totalScore(totalScore)
                .periodScores(periodScores)
                .periodWeights(periodWeights)
                .scoreWeight(scoreWeight)
                .build();
    }

    private Map<Integer, Integer> getPeriodRank(String exptGroupId, ExptSandReportData exptData) {
        ExperimentRankResponse experimentRankResponse = exptData.getExperimentRankResponse();
        if (BeanUtil.isEmpty(experimentRankResponse)) {
            throw new BizException("");
        }

        Map<Integer, Integer> result = new HashMap<>();
        // 获取期数排行榜
        List<ExperimentRankItemResponse> periodRank = experimentRankResponse.getExperimentRankItemResponseList();
        // 期数排行榜按照期数分组
        Map<Integer, List<ExperimentRankGroupItemResponse>> periodMapRank = periodRank.stream()
                .collect(Collectors.toMap(ExperimentRankItemResponse::getPeriods, ExperimentRankItemResponse::getExperimentRankGroupItemResponseList));
        for (int i = 0; i < periodRank.size(); i++) {
            Integer period = i + 1;
            List<ExperimentRankGroupItemResponse> periodRankList = periodMapRank.get(period);
            Integer rankNum = 0;
            for (int j = 0; j < periodRankList.size(); j++) {
                ExperimentRankGroupItemResponse itemRank = periodRankList.get(j);
                if (exptGroupId.equals(itemRank.getExperimentGroupId())) {
                    rankNum = j + 1;
                }
            }

            result.put(period, rankNum);
        }

        return result;
    }

    private Integer getTotalRank(String exptGroupId, ExptSandReportData exptData) {
        ExperimentRankResponse experimentRankResponse = exptData.getExperimentRankResponse();
        List<ExperimentTotalRankItemResponse> totalRank = experimentRankResponse.getExperimentTotalRankItemResponseList();
        Integer rankNum = 0;
        for (int i = 0; i < totalRank.size(); i++) {
            ExperimentTotalRankItemResponse itemRank = totalRank.get(i);
            if (exptGroupId.equals(itemRank.getExperimentGroupId())) {
                rankNum = i + 1;
            }
        }
        return rankNum;
    }

    private ExptSandReportModel.ScoreInfo getEmptyScoreInfo(String exptGroupId, ExptSandReportData exptData) {
        // 每期权重
        List<ExptSandReportModel.ScoreInfo.PeriodWeight> periodWeights = new ArrayList<>();
        ExperimentSetting.SandSetting sandSetting = exptData.getSandSetting();
        Map<String, Float> weightMap = sandSetting.getWeightMap();
        weightMap.forEach((period, weight) -> {
            ExptSandReportModel.ScoreInfo.PeriodWeight periodWeight = ExptSandReportModel.ScoreInfo.PeriodWeight.builder()
                    .periods(period)
                    .weight(String.valueOf(weight == null ? 0.00 : weight) + "%")
                    .build();
            periodWeights.add(periodWeight);
        });

        // 评分权重
        Float healthIndexWeight = sandSetting.getHealthIndexWeight();
        Float knowledgeWeight = sandSetting.getKnowledgeWeight();
        Float medicalRatioWeight = sandSetting.getMedicalRatioWeight();
        ExptSandReportModel.ScoreInfo.ScoreWeight scoreWeight = ExptSandReportModel.ScoreInfo.ScoreWeight.builder()
                .healthIndexWeight(String.valueOf(healthIndexWeight == null ? 0.00 : healthIndexWeight) + "%")
                .knowledgeWeight(String.valueOf(knowledgeWeight == null ? 0.00 : knowledgeWeight) + "%")
                .treatmentPercentWeight(String.valueOf(medicalRatioWeight == null ? 0.00 : medicalRatioWeight) + "%")
                .build();

        // 期数得分信息
        List<ExptSandReportModel.ScoreInfo.PeriodScore> periodScores = new ArrayList<>();
        Integer periods = sandSetting.getPeriods();
        for (int i = 0; i < periods; i++) {
            ExptSandReportModel.ScoreInfo.Score scoreInfo = ExptSandReportModel.ScoreInfo.Score.builder()
                    .healthIndexScore("0.00")
                    .knowledgeScore("0.00")
                    .treatmentPercentScore("0.00")
                    .totalScore("0.00")
                    // todo 计算下
                    .totalRanking("0")
                    .build();
            ExptSandReportModel.ScoreInfo.PeriodScore periodScore = ExptSandReportModel.ScoreInfo.PeriodScore.builder()
                    .periods(String.valueOf(i + 1))
                    .scoreInfo(scoreInfo)
                    .build();
            periodScores.add(periodScore);
        }

        // 总得分
        ExptSandReportModel.ScoreInfo.Score totalScore = ExptSandReportModel.ScoreInfo.Score.builder()
                .healthIndexScore("0.00")
                .knowledgeScore("0.00")
                .treatmentPercentScore("0.00")
                .totalScore("0.00")
                .totalRanking("1")
                .build();

        return ExptSandReportModel.ScoreInfo.builder()
                .periodWeights(periodWeights)
                .scoreWeight(scoreWeight)
                .totalScore(totalScore)
                .periodScores(periodScores)
                .build();
    }

    private List<ExptSandReportModel.NpcData> generateNpcInfo(String exptGroupId, ExptSandReportData exptData) {
        List<ExptSandReportModel.NpcData> result = new ArrayList<>();
        String experimentInstanceId = exptData.getExptInfo().getExperimentInstanceId();

        // 获取NPC指标数据
        Map<Integer, List<PersonRiskFactor>> collect = new HashMap<>();
        try {
            collect = riskBiz.get(experimentInstanceId, exptGroupId, null).stream()
                    .collect(Collectors.groupingBy(PersonRiskFactor::getPeriod));
        } catch (Exception e) {
            log.error(String.format("生成报告时，获取 NPC 数据异常，异常是：%s", e));
            return result;
        }
        if (ShareUtil.XObject.isEmpty(collect)) {
            return result;
        }
        // 获取第0期
        List<PersonRiskFactor> personRiskFactors1 = collect.get(0);
        // 取最后一期
        List<PersonRiskFactor> personRiskFactors2 = collect.get(collect.size() - 1);
        // 人物ID->NPC人物对象
        Map<String, ExptSandReportModel.NpcData> npcData = new LinkedHashMap<>();

        for (PersonRiskFactor personRiskFactor : personRiskFactors1) {
            ExptSandReportModel.NpcData nd = new ExptSandReportModel.NpcData();
            nd.setInterveneBefores(personRiskFactor);
            npcData.put(personRiskFactor.getPersonId(), nd);
            result.add(nd);
        }
        for (PersonRiskFactor personRiskFactor : personRiskFactors2) {
            ExptSandReportModel.NpcData npcData1 = npcData.get(personRiskFactor.getPersonId());
            if (npcData1 != null) {
                npcData1.setInterveneAfters(personRiskFactor);
            }
        }

        Set<String> strings = npcData.keySet();
        for (String personId : strings) {
            // todo 获取实验人物的服务记录
            List<OperateFlowEntity> operateFlowEntities = experimentOrgBiz
                    .listFlowLog(experimentInstanceId, personId);
            ExptSandReportModel.NpcData npcData1 = npcData.get(personId);

            for (OperateFlowEntity operateFlowEntity : operateFlowEntities) {
                ExptSandReportModel.ServiceLog serviceLog = new ExptSandReportModel.ServiceLog();
                serviceLog.setDt(DateUtil.formatDateTime(operateFlowEntity.getOperateTime()));
                serviceLog.setLable(operateFlowEntity.getFlowName());
                serviceLog.setDescr(operateFlowEntity.getReportLabel());
                npcData1.getServiceLogs().add(serviceLog);
            }
        }
        return result;
    }

    private List<List<ExptSandReportModel.KnowledgeAnswer>> generatePeriodQuestionnaires(String exptGroupId, ExptSandReportData exptData) {
        List<ExperimentQuestionnaireResponse> exptQuestionnaireList = exptData.getExptQuestionnaireList();
        List<List<ExptSandReportModel.KnowledgeAnswer>> result = new ArrayList<>();
        if (CollUtil.isEmpty(exptQuestionnaireList)) {
            return result;
        }

        // filter knowledge-answer of group
        List<ExperimentQuestionnaireResponse> groupQuestionnaireList = exptQuestionnaireList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .toList();
        if (CollUtil.isEmpty(groupQuestionnaireList)) {
            return result;
        }

        // 按照期数分组
        Map<Integer, List<ExperimentQuestionnaireResponse>> periodCollect = groupQuestionnaireList.stream()
                .collect(Collectors.groupingBy(ExperimentQuestionnaireResponse::getPeriodSequence));
        int size = periodCollect.size();
        for (int i = 0; i < size; i++) {
            // 该期问题列表
            List<ExptSandReportModel.KnowledgeAnswer> periodResult = new ArrayList<>();
            List<ExperimentQuestionnaireResponse> periodQuestionnaires = periodCollect.get(i + 1);

            // merge questionnaire
            List<ExperimentQuestionnaireItemResponse> flatGroupQuestionnaireList = periodQuestionnaires.stream()
                    .flatMap(item -> item.getItemList().stream())
                    .toList();
            List<ExperimentQuestionnaireResponse.ExptCategQuestionnaireItem> categItemList = ExperimentQuestionnaireResponse.convertItemList2CategItemList(flatGroupQuestionnaireList);

            // build result
            categItemList.forEach(categItem -> {
                // 构建类目下题目列表信息
                List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> questionInfos = new ArrayList<>();
                List<ExperimentQuestionnaireItemResponse> itemList = categItem.getItemList();
                if (CollUtil.isNotEmpty(itemList)) {
                    questionInfos = buildQuestionItem(itemList);
                }

                // build knowledgeAnswer
                ExptSandReportModel.KnowledgeAnswer knowledgeAnswer = ExptSandReportModel.KnowledgeAnswer.builder()
                        .categName(categItem.getCategName())
                        .questionInfos(questionInfos)
                        .build();
                periodResult.add(knowledgeAnswer);
            });

            result.add(periodResult);
        }
        return result;
    }

    private List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> buildQuestionItem(List<ExperimentQuestionnaireItemResponse> itemList) {
        List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> result = new ArrayList<>();
        itemList.forEach(questionItem -> {
            // 处理子节点
            List<ExperimentQuestionnaireItemResponse> questionItemChildren = questionItem.getChildren();
            List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> children = new ArrayList<>();
            if (CollUtil.isNotEmpty(questionItemChildren)) {
                children = convertQuestionChildren(questionItem.getChildren());
            }

            // 处理当前节点
            ExptSandReportModel.KnowledgeAnswer.QuestionInfo questionInfo = handleNode(questionItem, children);
            result.add(questionInfo);
        });

        return result;
    }

    private List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> convertQuestionChildren(List<ExperimentQuestionnaireItemResponse> oriChildren) {
        if (CollUtil.isEmpty(oriChildren)) {
            return new ArrayList<>();
        }

        List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> result = new ArrayList<>();
        oriChildren.forEach(oriChild -> {
            // 处理子节点
            List<ExperimentQuestionnaireItemResponse> itemOriChildren = oriChild.getChildren();
            List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> itemTargetChildren = new ArrayList<>();
            if (CollUtil.isNotEmpty(itemOriChildren)) {
                itemTargetChildren = convertQuestionChildren(itemOriChildren);
            }

            // 处理当前节点
            ExptSandReportModel.KnowledgeAnswer.QuestionInfo resultItem = handleNode(oriChild, itemTargetChildren);
            result.add(resultItem);
        });

        return result;
    }

    private ExptSandReportModel.KnowledgeAnswer.QuestionInfo handleNode(ExperimentQuestionnaireItemResponse questionItem, List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> children) {
        // 构造标题
        String questionType = questionItem.getQuestionType();
        String typeName = QuestionTypeEnum.getNameByCode(questionType);
        String questionTitle = "[" + typeName + "]" + questionItem.getQuestionTitle();

        // 构建选项
        List<ExptQuestionnaireOptionDTO> questionOptionList = questionItem.getQuestionOptionList();
        List<String> questionOptions = new ArrayList<>();
        if (CollUtil.isNotEmpty(questionOptionList)) {
            questionOptions = questionOptionList.stream()
                    .map(option -> {
                        String title = option.getTitle();
                        String value = option.getValue();
                        return title + " " + value;
                    }).toList();
        }

        // 构造回答答案
        String userAnswer = null;
        List<String> questionResult = questionItem.getQuestionResult();
        if (CollUtil.isNotEmpty(questionResult)) {
            Map<String, String> questionMap = questionResult.stream().collect(Collectors.toMap(item -> item, item -> item, (v1, v2) -> v1));
            userAnswer = questionOptionList.stream()
                    .filter(item -> {
                        return questionMap.get(item.getId()) != null;
                    }).map(ExptQuestionnaireOptionDTO::getTitle)
                    .collect(Collectors.joining());
        }

        // 构造正确答案
        String rightAnswer = null;
        String rightValue = questionItem.getRightValue();
        if (StrUtil.isNotBlank(rightValue)) {
            List<ExptQuestionnaireOptionDTO> rightValueList = JSONUtil.toList(rightValue, ExptQuestionnaireOptionDTO.class);
            if (CollUtil.isNotEmpty(rightValueList)) {
                rightAnswer = rightValueList.stream()
                        .map(ExptQuestionnaireOptionDTO::getTitle)
                        .collect(Collectors.joining());
            }
        }

        // 构造答案解析
        String analysis = questionItem.getQuestionDetailedAnswer();

        // build
        return ExptSandReportModel.KnowledgeAnswer.QuestionInfo.builder()
                .questionTitle(questionTitle)
                .questionOptions(questionOptions)
                .userAnswer(userAnswer)
                .rightAnswer(rightAnswer)
                .analysis(analysis)
                .children(children)
                .build();
    }

}
