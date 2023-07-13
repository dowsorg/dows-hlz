package org.dows.hep.biz.report.pdf;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.itextpdf.commons.utils.Base64;
import com.itextpdf.styledxmlparser.jsoup.Jsoup;
import com.itextpdf.styledxmlparser.jsoup.nodes.Document;
import com.itextpdf.styledxmlparser.jsoup.nodes.Element;
import com.itextpdf.styledxmlparser.jsoup.select.Elements;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.constant.SystemConstant;
import org.dows.hep.api.report.pdf.*;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeItemResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;
import org.dows.hep.biz.base.oss.OSSBiz;
import org.dows.hep.biz.tenant.experiment.ExperimentSchemeScoreBiz;
import org.dows.hep.biz.user.experiment.ExperimentSchemeBiz;
import org.dows.hep.entity.ExperimentGroupEntity;
import org.dows.hep.entity.ExperimentInstanceEntity;
import org.dows.hep.entity.ExperimentParticipatorEntity;
import org.dows.hep.service.ExperimentGroupService;
import org.dows.hep.service.ExperimentInstanceService;
import org.dows.hep.service.ExperimentParticipatorService;
import org.springframework.core.io.ClassPathResource;
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
public class ExptSchemeReportBiz implements ExptReportBiz {

    private final Template2PdfBiz template2PdfBiz;
    private final FindSoftProperties findSoftProperties;
    private final ExperimentSchemeScoreBiz experimentSchemeScoreBiz;
    private final ExperimentSchemeBiz experimentSchemeBiz;
    private final OSSBiz ossBiz;
    private final ExperimentInstanceService experimentInstanceService;
    private final ExperimentParticipatorService experimentParticipatorService;
    private final ExperimentGroupService experimentGroupService;

    private ExperimentInstanceEntity exptInfo;
    private List<ExperimentSchemeResponse> exptSchemeList;
    private List<ExperimentGroupEntity> exptGroupInfoList;
    private List<ExperimentParticipatorEntity> exptMemberList;

    /**
     * @param experimentInstanceId - 实验ID
     * @param exptGroupId          - 小组ID
     * @author fhb
     * @description 如果有 exptGroupId，则只导出该组的报告，否则导出所有组的报告
     * @date 2023/7/7 16:54
     */
    @Override
    public ExptReportVO generatePdfReport(String experimentInstanceId, String exptGroupId) {
        List<ExptGroupReportVO> groupReportVOS = new ArrayList<>();
        ExptReportVO result = ExptReportVO.builder()
                .groupReportList(groupReportVOS)
                .build();

        // get expt-info
        this.exptInfo = getExptInfo(experimentInstanceId);
        this.exptGroupInfoList = listExptGroupInfo(experimentInstanceId, exptGroupId);
        this.exptMemberList = listExptMembers(experimentInstanceId, exptGroupId);
        this.exptSchemeList = listExptScheme(experimentInstanceId, exptGroupId);

        if (StrUtil.isBlank(exptGroupId)) { // 批量-所有小组
            for (ExperimentGroupEntity group : exptGroupInfoList) {
                ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(group.getExperimentGroupId());
                groupReportVOS.add(exptGroupReportVO);
            }
        } else { // 单个小组
            ExptGroupReportVO exptGroupReportVO = generatePdfReportOfGroup(exptGroupId);
            groupReportVOS.add(exptGroupReportVO);
        }

        return result;
    }

    private ExptGroupReportVO generatePdfReportOfGroup(String exptGroupId) {
        // pdf data
        ExptBaseInfo baseInfoVO = generateBaseInfoVO();
        ExptSchemeReportModel.GroupInfo groupInfo = generateGroupInfo(exptGroupId);
        ExptSchemeReportModel.ScoreInfo scoreInfo = generateScoreInfo(exptGroupId);
        ExptSchemeReportModel.SchemeInfo schemeInfo = generateSchemeInfo(exptGroupId);
        List<ExptSchemeReportModel.QuestionInfo> questionInfoList = generateQuestionInfo(exptGroupId);
        ExptSchemeReportModel pdfVO = ExptSchemeReportModel.builder()
                .baseInfo(baseInfoVO)
                .groupInfo(groupInfo)
                .scoreInfo(scoreInfo)
                .schemeInfo(schemeInfo)
                .questionInfos(questionInfoList)
                .build();

        // pdf file
        File targetFile = getFile(exptGroupId);
        // pdf flt
        String schemeFlt = getSchemeFlt();

        try {
            template2PdfBiz.convert2Pdf(pdfVO, schemeFlt, targetFile);
        } catch (IOException | TemplateException e) {
            log.error("导出方案设计报告时，html转pdf异常");
            throw new BizException("导出方案设计报告时，html转pdf异常");
        }

        List<String> paths = new ArrayList<>();
        paths.add(targetFile.getPath());
        return ExptGroupReportVO.builder()
                .exptGroupId(exptGroupId)
                .exptGroupNo(Integer.valueOf(groupInfo.getGroupNo()))
                .paths(paths)
                .build();
    }

    private ExperimentInstanceEntity getExptInfo(String exptInstanceId) {
        return experimentInstanceService.lambdaQuery()
                .eq(ExperimentInstanceEntity::getExperimentInstanceId, exptInstanceId)
                .oneOpt()
                .orElse(null);
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

    private List<ExperimentGroupEntity> listExptGroupInfo(String exptInstanceId, String exptGroupId) {
        return experimentGroupService.lambdaQuery()
                .eq(ExperimentGroupEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentGroupEntity::getExperimentGroupId, exptGroupId)
                .list();
    }

    private List<ExperimentParticipatorEntity> listExptMembers(String exptInstanceId, String exptGroupId) {
        return experimentParticipatorService.lambdaQuery()
                .eq(ExperimentParticipatorEntity::getExperimentInstanceId, exptInstanceId)
                .eq(StrUtil.isNotBlank(exptGroupId), ExperimentParticipatorEntity::getExperimentGroupId, exptGroupId)
                .list();
    }

    private ExptBaseInfo generateBaseInfoVO() {
        String logoStr = null;
        String coverStr = null;
        try {
            logoStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getLogo()).getInputStream()));
            coverStr = Base64.encodeBytes(IOUtils.toByteArray(new ClassPathResource(findSoftProperties.getCover()).getInputStream()));
        } catch (IOException e) {
            log.error("导出实验报告时，获取logo和cover图片资源异常");
            throw new BizException("导出实验报告时，获取logo和cover图片资源异常");
        }

        return ExptBaseInfo.builder()
                .title(findSoftProperties.getExptSchemeReportTitle())
                .logoImg(logoStr)
                .coverImg(coverStr)
                .copyRight(findSoftProperties.getCopyRight())
                .build();
    }

    private ExptSchemeReportModel.GroupInfo generateGroupInfo(String exptGroupId) {
        ExperimentInstanceEntity exptInfo = this.exptInfo;
        List<ExperimentGroupEntity> groupList = this.exptGroupInfoList;
        List<ExperimentParticipatorEntity> memberList = this.exptMemberList;
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

    // todo 获取得分信息
    private ExptSchemeReportModel.ScoreInfo generateScoreInfo(String exptGroupId) {
        return ExptSchemeReportModel.ScoreInfo.builder()
                .show(Boolean.TRUE)
                .score(0.0f)
                .ranking(1)
                .build();
    }

    private ExptSchemeReportModel.SchemeInfo generateSchemeInfo(String exptGroupId) {
        List<ExperimentSchemeResponse> schemeList = this.exptSchemeList;
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

    private List<ExptSchemeReportModel.QuestionInfo> generateQuestionInfo(String exptGroupId) {
        List<ExptSchemeReportModel.QuestionInfo> result = new ArrayList<>();
        List<ExperimentSchemeResponse> schemeResponseList = this.exptSchemeList;
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
            src = src.replace("/hepapi/", "");
            imgTag.attr("src", ossBiz.getBase64(src));
        }

        // 返回替换后的文本
        return doc.body().html();
    }
//
//    private String getBase64(String pathName) {
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        ossBiz.downloadByPath(outputStream, pathName);
//        byte[] byteArray = outputStream.toByteArray();
//        return Base64.encodeBytes(byteArray);
//    }

    private File getFile(String exptGroupId) {
        ExperimentInstanceEntity exptInfo = this.exptInfo;
        List<ExperimentGroupEntity> groupList = this.exptGroupInfoList;
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

        // todo 换成可配置的
        String fileName = "第" + groupEntity.getGroupNo() + "组" + SystemConstant.SPLIT_UNDER_LINE + exptInfo.getExperimentName() + SystemConstant.SPLIT_UNDER_LINE + "方案设计报告" + SystemConstant.SUFFIX_PDF;
        File homeDirFile = new File(SystemConstant.PDF_REPORT_PATH);
        homeDirFile.mkdirs();
        return new File(homeDirFile, fileName);
    }

    private String getSchemeFlt() {
        return findSoftProperties.getExptSchemeFtl();
    }

}
