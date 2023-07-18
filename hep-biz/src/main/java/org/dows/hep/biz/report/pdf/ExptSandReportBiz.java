package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.user.experiment.dto.ExptQuestionnaireOptionDTO;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentQuestionnaireResponse;
import org.dows.hep.biz.user.experiment.ExperimentQuestionnaireBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.entity.ExperimentScoringEntity;
import org.dows.hep.properties.FindSoftProperties;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.service.ExperimentScoringService;
import org.dows.hep.vo.report.ExptBaseInfoModel;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.dows.hep.vo.report.ExptSandReportModel;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class ExptSandReportBiz implements ExptReportBiz<ExptSandReportBiz.ExptSandReportData, ExptSandReportModel> {

    private final Template2PdfBiz template2PdfBiz;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentQuestionnaireBiz experimentQuestionnaireBiz;
    private final FindSoftProperties findSoftProperties;
    private final ExperimentGroupService experimentGroupService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentScoringService experimentScoringService;

    @Data
    @Builder
    public static class ExptSandReportData implements ExptReportData {
        private ExperimentInstanceEntity exptInfo;
        private List<ExperimentGroupEntity> exptGroupInfoList;
        private List<ExperimentParticipatorEntity> exptMemberList;
        private List<ExperimentScoringEntity> exptScoringList;
        private ExperimentSetting.SandSetting sandSetting;
        private List<ExperimentQuestionnaireResponse> exptQuestionnaireList;
    }

    /**
     * @param experimentInstanceId - 实验实例ID
     * @param exptGroupId          - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 如果有 exptGroupId，则只导出该组的报告，否则导出所有组的报告
     * @date 2023/7/17 9:23
     */
    @Override
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) {
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
                ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(group.getExperimentGroupId(), exptData);
                groupReportVOs.add(exptGroupReportVO);
            }
        } else { // 单个小组
            ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(exptGroupId, exptData);
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
                .exptQuestionnaireList(listExptQuestionnaire(exptInstanceId, exptGroupId))
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
    public ExptSandReportModel getExptReportModel(String exptGroupId, ExptSandReportData exptData) {
        ExptBaseInfoModel baseInfoVO = generateBaseInfoVO(findSoftProperties, log);
        ExptSandReportModel.GroupInfo groupInfo = generateGroupInfo(exptGroupId, exptData);
        ExptSandReportModel.ScoreInfo scoreInfo = generateScoreInfo(exptGroupId, exptData);
        List<ExptSandReportModel.NpcData> npcDataList = generateNpcInfo(exptGroupId, exptData);
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
    public File getTempFile(String exptGroupId, ExptSandReportData exptReportData) {
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

        File homeDirFile = new File(SystemConstant.PDF_REPORT_TMP_PATH);
        boolean mkdirs = homeDirFile.mkdirs();
        String fileName = "第" + groupEntity.getGroupNo() + "组" + SystemConstant.SPLIT_UNDER_LINE + exptInfo.getExperimentName() + SystemConstant.SPLIT_UNDER_LINE + "沙盘模拟报告" + SystemConstant.SUFFIX_PDF;
        return new File(homeDirFile, fileName);
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
    private ExptGroupReportVO generatePdfReportOfGroup(String exptGroupId, ExptSandReportData exptData) {
        // 将 expt-data 转为 pdf-data
        ExptSandReportModel pdfVO = getExptReportModel(exptGroupId, exptData);
        // pdf file
        File targetFile = getTempFile(exptGroupId, exptData);
        // pdf flt
        String schemeFlt = getSchemeFlt();

        try {
            template2PdfBiz.convert2Pdf(pdfVO, schemeFlt, targetFile);
        } catch (IOException | TemplateException e) {
            log.error("导出沙盘模拟报告时，html转pdf异常");
            throw new BizException("导出沙盘模拟报告时，html转pdf异常");
        }

        List<ExptGroupReportVO.ReportFile> paths = new ArrayList<>();
        ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                .name(targetFile.getName())
                .path(targetFile.getPath())
                .build();
        paths.add(reportFile);
        return ExptGroupReportVO.builder()
                .exptGroupId(exptGroupId)
                .exptGroupNo(Integer.valueOf(pdfVO.getGroupInfo().getGroupNo()))
                .paths(paths)
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

    private List<ExperimentQuestionnaireResponse> listExptQuestionnaire(String exptInstanceId, String exptGroupId) {
        return experimentQuestionnaireBiz.listExptQuestionnaire(exptInstanceId, exptGroupId, Boolean.TRUE);
    }

    private ExptSandReportModel.GroupInfo generateGroupInfo(String exptGroupId, ExptSandReportData exptData) {
        ExperimentInstanceEntity exptInfo = exptData.getExptInfo();
        List<ExperimentGroupEntity> groupList = exptData.getExptGroupInfoList();
        List<ExperimentParticipatorEntity> memberList = exptData.getExptMemberList();
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

        return ExptSandReportModel.GroupInfo.builder()
                .groupNo(groupEntity.getGroupNo())
                .groupName(groupEntity.getGroupName())
                .groupMembers(groupMembers)
                .caseName(exptInfo.getCaseName())
                .experimentName(exptInfo.getExperimentName())
                .exptStartDate(exptInfo.getStartTime())
                // todo 后续改为查询
                .caseNum(3)
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

        // 期数得分信息
        List<ExptSandReportModel.ScoreInfo.PeriodScore> periodScores = new ArrayList<>();
        Map<Integer, ExperimentScoringEntity> periodMapScore = groupScoreList.stream()
                .collect(Collectors.toMap(ExperimentScoringEntity::getPeriods, item -> item, (v1, v2) -> v1));
        periodMapScore.forEach((k, v) -> {
            ExptSandReportModel.ScoreInfo.Score scoreInfo = ExptSandReportModel.ScoreInfo.Score.builder()
                    .healthIndexScore(v.getHealthIndexScore())
                    .knowledgeScore(v.getKnowledgeScore())
                    .treatmentPercentScore(v.getTreatmentPercentScore())
                    .totalScore(v.getTotalScore())
                    // todo 计算下
                    .totalRanking("0")
                    .build();
            ExptSandReportModel.ScoreInfo.PeriodScore periodScore = ExptSandReportModel.ScoreInfo.PeriodScore.builder()
                    .periods(String.valueOf(k))
                    .scoreInfo(scoreInfo)
                    .build();
            periodScores.add(periodScore);
        });

        // todo 计算 还是 存储？
        // 总得分
        ExptSandReportModel.ScoreInfo.Score totalScore = ExptSandReportModel.ScoreInfo.Score.builder()
                .healthIndexScore("0.00")
                .knowledgeScore("0.00")
                .treatmentPercentScore("0.00")
                .totalScore("0.00")
                .totalRanking("1")
                .build();

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

        return ExptSandReportModel.ScoreInfo.builder()
                .totalScore(totalScore)
                .periodScores(periodScores)
                .periodWeights(periodWeights)
                .scoreWeight(scoreWeight)
                .build();
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

    private static ExptSandReportModel.KnowledgeAnswer.QuestionInfo handleNode(ExperimentQuestionnaireItemResponse questionItem, List<ExptSandReportModel.KnowledgeAnswer.QuestionInfo> children) {
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
