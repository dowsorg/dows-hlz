package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import freemarker.template.TemplateException;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.api.user.experiment.response.ExptSchemeScoreRankResponse;
import org.dows.hep.biz.base.oss.OSSBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeScoreBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.properties.FindSoftProperties;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.dows.hep.vo.report.ExptBaseInfoModel;
import org.dows.hep.vo.report.ExptGroupReportVO;
import org.dows.hep.vo.report.ExptReportVO;
import org.dows.hep.vo.report.ExptSchemeReportModel;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验`方案设计报告`biz
 * @date 2023/7/7 10:21
 **/
@Slf4j
@Component
@RequiredArgsConstructor
public class ExptSchemeReportBiz implements ExptReportBiz<ExptSchemeReportBiz.ExptSchemeReportData, ExptSchemeReportModel> {

    private final Template2PdfBiz template2PdfBiz;
    private final FindSoftProperties findSoftProperties;
    private final ExperimentSchemeScoreBiz experimentSchemeScoreBiz;
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final OSSBiz ossBiz;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentGroupService experimentGroupService;

    @Data
    @Builder
    public static class ExptSchemeReportData implements ExptReportData{
        private ExperimentInstanceEntity exptInfo;
        private List<ExperimentSchemeResponse> exptSchemeList;
        private List<ExperimentGroupEntity> exptGroupInfoList;
        private List<ExperimentParticipatorEntity> exptMemberList;
        private List<ExptSchemeScoreRankResponse> rankList;
    }

    /**
     * @param experimentInstanceId - 实验ID
     * @param exptGroupId          - 小组ID
     * @author fhb
     * @description 如果有 exptGroupId，则只导出该组的报告，否则导出所有组的报告
     * @date 2023/7/7 16:54
     */
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) {
        // 构建 result
        List<ExptGroupReportVO> groupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(groupReportVOS)
                .build();

        // 有小组ID则生成该小组的报告，
        // 没有小组ID则批量生成实验所有小组的报告
        ExptSchemeReportData exptData = prepareData(experimentInstanceId, exptGroupId);
        List<ExperimentGroupEntity> exptGroupInfoList = exptData.getExptGroupInfoList();
        if (StrUtil.isBlank(exptGroupId)) { // 批量-所有小组
            for (ExperimentGroupEntity group : exptGroupInfoList) {
                ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(group.getExperimentGroupId(), exptData);
                groupReportVOS.add(exptGroupReportVO);
            }
        } else { // 单个小组
            ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(exptGroupId, exptData);
            groupReportVOS.add(exptGroupReportVO);
        }

        return result;
    }

    /**
     * @param exptInstanceId - 实验实例ID
     * @param exptGroupId - 实验小组ID
     * @return org.dows.hep.biz.report.pdf.ExptSchemeReportBiz.ExptSchemeReportData
     * @author fhb
     * @description 预先准备好生成报告需要的数据
     * @date 2023/7/17 11:07
     */
    @Override
    public ExptSchemeReportData prepareData(String exptInstanceId, String exptGroupId) {
        // 准备`生成报告`所需的数据
        return ExptSchemeReportData.builder()
                .exptInfo(getExptInfo(experimentInstanceService, exptInstanceId))
                .exptSchemeList(listExptScheme(exptInstanceId, exptGroupId))
                .exptGroupInfoList(listExptGroupInfo(experimentGroupService, exptInstanceId, exptGroupId))
                .exptMemberList(listExptMembers(experimentParticipatorService, exptInstanceId, exptGroupId))
                .rankList(listSchemeRank(exptInstanceId))
                .build();
    }

    /**
     * @param exptGroupId - 实验小组ID
     * @param exptData - 生成 `填充模板数据model` 需要的数据支持
     * @return org.dows.hep.vo.report.ExptSchemeReportModel
     * @author fhb
     * @description 生成pdf所需要填充的数据
     * @date 2023/7/17 11:09
     */
    @Override
    public ExptSchemeReportModel getExptReportModel(String exptGroupId, ExptSchemeReportData exptData) {
        ExptBaseInfoModel baseInfoVO = generateBaseInfoVO(findSoftProperties, log);
        ExptSchemeReportModel.GroupInfo groupInfo = generateGroupInfo(exptGroupId, exptData);
        ExptSchemeReportModel.ScoreInfo scoreInfo = generateScoreInfo(exptGroupId, exptData);
        ExptSchemeReportModel.SchemeInfo schemeInfo = generateSchemeInfo(exptGroupId, exptData);
        List<ExptSchemeReportModel.QuestionInfo> questionInfoList = generateQuestionInfo(exptGroupId, exptData);

        return ExptSchemeReportModel.builder()
                .baseInfo(baseInfoVO)
                .groupInfo(groupInfo)
                .scoreInfo(scoreInfo)
                .schemeInfo(schemeInfo)
                .questionInfos(questionInfoList)
                .build();
    }

    /**
     * @param exptGroupId - 实验小组ID
     * @param exptReportData - 生成 `填充模板数据model` 需要的数据支持
     * @return java.io.File
     * @author fhb
     * @description pdf 生成的位置
     * @date 2023/7/17 11:11
     */
    @Override
    public File getTempFile(String exptGroupId, ExptSchemeReportData exptReportData) {
        ExperimentInstanceEntity exptInfo = exptReportData.getExptInfo();
        List<ExperimentGroupEntity> groupList = exptReportData.getExptGroupInfoList();
        if (CollUtil.isEmpty(groupList) || StrUtil.isBlank(exptGroupId)) {
            throw new BizException("获取实验方案设计报告时，获取组员信息数据异常");
        }

        ExperimentGroupEntity groupEntity = groupList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .findFirst()
                .orElse(null);
        if (BeanUtil.isEmpty(groupEntity)) {
            throw new BizException("获取实验方案设计报告时，获取组员信息数据异常");
        }

        File homeDirFile = new File(SystemConstant.PDF_REPORT_TMP_PATH);
        boolean mkdirs = homeDirFile.mkdirs();
        String fileName = "第" + groupEntity.getGroupNo() + "组" + SystemConstant.SPLIT_UNDER_LINE + exptInfo.getExperimentName() + SystemConstant.SPLIT_UNDER_LINE + "方案设计报告" + SystemConstant.SUFFIX_PDF;
        return new File(homeDirFile, fileName);
    }

    /**
     * @return java.lang.String
     * @author fhb
     * @description 获取pdf模板
     * @date 2023/7/17 11:11
     */
    @Override
    public String getSchemeFlt() {
        return findSoftProperties.getExptSchemeFtl();
    }

    // 生成 pdf 报告
    // todo 如果文件存在，则不再生成立即返回，测试阶段先不做
    private ExptGroupReportVO generatePdfReportOfGroup(String exptGroupId, ExptSchemeReportData exptData) {
        // pdf 填充数据
        ExptSchemeReportModel pdfVO = getExptReportModel(exptGroupId, exptData);
        // pdf 输出文件
        File targetFile = getTempFile(exptGroupId, exptData);
        // pdf 模板
        String schemeFlt = getSchemeFlt();

        try {
            template2PdfBiz.convert2Pdf(pdfVO, schemeFlt, targetFile);
        } catch (IOException | TemplateException e) {
            log.error("导出方案设计报告时，html转pdf异常");
            throw new BizException("导出方案设计报告时，html转pdf异常");
        }

        ExptGroupReportVO.ReportFile reportFile = ExptGroupReportVO.ReportFile.builder()
                .name(targetFile.getName())
                .path(targetFile.getPath())
                .build();
        List<ExptGroupReportVO.ReportFile> paths = List.of(reportFile);
        return ExptGroupReportVO.builder()
                .exptGroupId(exptGroupId)
                .exptGroupNo(Integer.valueOf(pdfVO.getGroupInfo().getGroupNo()))
                .paths(paths)
                .build();
    }

    private List<ExperimentSchemeResponse> listExptScheme(String exptInstanceId, String exptGroupId) {
        List<ExperimentSchemeResponse> schemeResponseList = experimentSchemeBiz.listScheme(exptInstanceId);
        if (CollUtil.isEmpty(schemeResponseList)) {
            return schemeResponseList;
        }
        if (StrUtil.isBlank(exptGroupId)) {
            return schemeResponseList;
        }

        return schemeResponseList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .toList();
    }

    private List<ExptSchemeScoreRankResponse> listSchemeRank(String exptInstanceId) {
        return experimentSchemeBiz.listExptSchemeScoreRank(exptInstanceId);
    }

    private ExptSchemeReportModel.GroupInfo generateGroupInfo(String exptGroupId, ExptSchemeReportData exptSchemeReportData) {
        ExperimentInstanceEntity exptInfo = exptSchemeReportData.getExptInfo();
        List<ExperimentGroupEntity> groupList = exptSchemeReportData.getExptGroupInfoList();
        List<ExperimentParticipatorEntity> memberList = exptSchemeReportData.getExptMemberList();
        final ExptSchemeReportModel.GroupInfo emptyGroupInfo = new ExptSchemeReportModel.GroupInfo();

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


        return ExptSchemeReportModel.GroupInfo.builder()
                .groupNo(groupEntity.getGroupNo())
                .groupName(groupEntity.getGroupName())
                .groupMembers(groupMembers)
                .caseName(exptInfo.getCaseName())
                .experimentName(exptInfo.getExperimentName())
                .exptStartDate(exptInfo.getStartTime())
                .build();
    }

    private ExptSchemeReportModel.ScoreInfo generateScoreInfo(String exptGroupId, ExptSchemeReportData exptSchemeReportData) {
        List<ExptSchemeScoreRankResponse> rankList = exptSchemeReportData.getRankList();
        if (CollUtil.isEmpty(rankList)) {
            return ExptSchemeReportModel.ScoreInfo.builder()
                    .show(Boolean.TRUE)
                    .score("0.00")
                    .ranking(0)
                    .build();
        }

        int rank = 0;
        String score = "0.00";
        for (int i = 0; i < rankList.size(); i++) {
            ExptSchemeScoreRankResponse itemRank = rankList.get(i);
            if (exptGroupId.equals(itemRank.getGroupId())) {
                rank = i + 1;
                score = itemRank.getScore();
            }
        }

        return ExptSchemeReportModel.ScoreInfo.builder()
                .show(Boolean.TRUE)
                .score(score)
                .ranking(rank)
                .build();
    }

    private ExptSchemeReportModel.SchemeInfo generateSchemeInfo(String exptGroupId, ExptSchemeReportData exptSchemeReportData) {
        List<ExperimentSchemeResponse> schemeList = exptSchemeReportData.getExptSchemeList();
        final ExptSchemeReportModel.SchemeInfo emptySchemeInfo = new ExptSchemeReportModel.SchemeInfo();
        if (CollUtil.isEmpty(schemeList)) {
            return emptySchemeInfo;
        }

        // filter group-info
        ExperimentSchemeResponse schemeResponse = schemeList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .findFirst()
                .orElse(null);
        if (BeanUtil.isEmpty(schemeResponse)) {
            return emptySchemeInfo;
        }

        return ExptSchemeReportModel.SchemeInfo.builder()
                .schemeName(schemeResponse.getSchemeName())
                .schemeTips(schemeResponse.getSchemeTips())
                .schemeDescr(schemeResponse.getSchemeDescr())
                .build();
    }

    private List<ExptSchemeReportModel.QuestionInfo> generateQuestionInfo(String exptGroupId, ExptSchemeReportData exptSchemeReportData) {
        List<ExptSchemeReportModel.QuestionInfo> result = new ArrayList<>();
        List<ExperimentSchemeResponse> schemeResponseList = exptSchemeReportData.getExptSchemeList();
        if (CollUtil.isEmpty(schemeResponseList)) {
            return result;
        }

        ExperimentSchemeResponse schemeResponse = schemeResponseList.stream()
                .filter(item -> exptGroupId.equals(item.getExperimentGroupId()))
                .findFirst()
                .orElse(null);
        if (BeanUtil.isEmpty(schemeResponse)) {
            return result;
        }

        List<ExperimentSchemeItemResponse> questionList = schemeResponse.getItemList();
        questionList.forEach(question -> {
            List<ExptSchemeReportModel.QuestionInfo> children = convertQuestionChildren(question.getChildren());
            ExptSchemeReportModel.QuestionInfo resultItem = ExptSchemeReportModel.QuestionInfo.builder()
                    .questionTitle(question.getQuestionTitle())
                    .questionDescr(convertImg2Base64(question.getQuestionDescr()))
                    .questionResult(convertImg2Base64(question.getQuestionResult()))
                    .children(children)
                    .build();
            result.add(resultItem);
        });

        return result;
    }

    private List<ExptSchemeReportModel.QuestionInfo> convertQuestionChildren(List<ExperimentSchemeItemResponse> oriChildren) {
        if (CollUtil.isEmpty(oriChildren)) {
            return new ArrayList<>();
        }

        List<ExptSchemeReportModel.QuestionInfo> result = new ArrayList<>();
        oriChildren.forEach(oriChild -> {
            List<ExperimentSchemeItemResponse> itemOriChildren = oriChild.getChildren();
            List<ExptSchemeReportModel.QuestionInfo> itemTargetChildren = convertQuestionChildren(itemOriChildren);
            ExptSchemeReportModel.QuestionInfo resultItem = ExptSchemeReportModel.QuestionInfo.builder()
                    .questionTitle(oriChild.getQuestionTitle())
                    .questionDescr(convertImg2Base64(oriChild.getQuestionDescr()))
                    .questionResult(convertImg2Base64(oriChild.getQuestionResult()))
                    .children(itemTargetChildren)
                    .build();
            result.add(resultItem);
        });

        return result;
    }

    private String convertImg2Base64(String text) {
        if (StrUtil.isBlank(text)) {
            return text;
        }

        Document doc = Jsoup.parse(text);
        Elements imgTags = doc.select("img");

        // 替换值并打印
        for (Element imgTag : imgTags) {
            String src = imgTag.attr("src");
            imgTag.attr("src", getBase64(src));
        }

        // 返回替换后的文本
        return doc.body().html();
    }

    private String getBase64(String src) {
        String fileName = src.replace("/hepapi/", "");
        String base64 = ossBiz.getBase64(fileName);
        return "data:image/jpeg;base64," + base64;
    }

}
