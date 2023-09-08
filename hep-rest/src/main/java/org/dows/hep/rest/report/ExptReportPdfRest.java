package org.dows.hep.rest.report;

import cn.hutool.core.date.DateUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.api.exceptions.BizException;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;
import org.dows.hep.api.tenant.experiment.request.ExptAccountReportRequest;
import org.dows.hep.api.tenant.experiment.request.ExptGroupReportPageRequest;
import org.dows.hep.api.tenant.experiment.request.ExptReportPageRequest;
import org.dows.hep.api.tenant.experiment.response.ExptAccountReportResponse;
import org.dows.hep.api.tenant.experiment.response.ExptGroupReportPageResponse;
import org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse;
import org.dows.hep.api.user.experiment.ExptSettingModeEnum;
import org.dows.hep.biz.report.ExptReportFacadeBiz;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.biz.user.experiment.ExperimentSettingBiz;
import org.dows.hep.properties.PdfServerProperties;
import org.dows.hep.vo.report.ExptReportModel;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author fhb
 * @version 1.0
 * @folder report
 * @description 报告管理-pdf形式
 * @date 2023/7/6 16:38
 **/
@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "pdf报告管理", description = "pdf报告管理")
public class ExptReportPdfRest {
    private final ExptReportFacadeBiz exptReportFacadeBiz;
    private final ExperimentSettingBiz experimentSettingBiz;
    private final ExperimentBaseBiz baseBiz;

    private final PdfServerProperties pdfServerProperties;

    /**
     * 分页获取报告列表
     *
     * @param pageRequest - 分页请求
     * @param request     - servletRequest
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse>
     * @author fhb
     * @description 分页获取报告列表
     * @date 2023/7/31 16:15
     */
    @Operation(summary = "分页获取报告列表")
    @PostMapping(value = "/v1/report/pageExptReport")
    public Page<ExptReportPageResponse> pageExptReport(@RequestBody @Validated ExptReportPageRequest pageRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return exptReportFacadeBiz.pageExptReport(pageRequest, accountId);
    }

    /**
     * 分页获取实验下小组列表
     *
     * @param pageRequest - 分页请求
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptGroupReportPageResponse>
     * @author fhb
     * @description 分页获取实验下小组列表
     * @date 2023/7/31 16:16
     */
    @Operation(summary = "分页获取实验下小组列表")
    @PostMapping(value = "/v1/report/pageGroupReport")
    public Page<ExptGroupReportPageResponse> pageGroupReport(@RequestBody @Validated ExptGroupReportPageRequest pageRequest) {
        return exptReportFacadeBiz.pageGroupReport(pageRequest);
    }

    /**
     * 分页获取学生报告列表
     *
     * @param pageRequest - 分页请求
     * @param request     - servletRequest
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptAccountReportResponse>
     * @author fhb
     * @description 分页获取学生报告列表
     * @date 2023/7/31 16:17
     */
    @Operation(summary = "分页获取学生报告列表")
    @PostMapping(value = "/v1/report/pageAccountReport")
    public Page<ExptAccountReportResponse> pageAccountReport(@RequestBody @Validated ExptAccountReportRequest pageRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        pageRequest.setAccountId(accountId);
        return exptReportFacadeBiz.pageAccountReport(pageRequest);
    }

    /**
     * 导出实验pdf报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出实验pdf报告
     * @date 2023/7/18 10:16
     */
    @Operation(summary = "导出实验pdf报告")
    @GetMapping(value = "/v1/report/exportExptReport")
    public ExptReportVO exportExptReport(@RequestParam String experimentInstanceId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        boolean regenerate = regenerate(experimentInstanceId, accountId);
        return exptReportFacadeBiz.exportExptReport(experimentInstanceId, accountId, Boolean.TRUE, regenerate, regenerate);
    }

    /**
     * 导出小组实验pdf报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId    - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出小组实验pdf报告
     * @date 2023/7/18 10:15
     */
    @Operation(summary = "导出小组实验pdf报告")
    @GetMapping(value = "/v1/report/exportGroupReport")
    public ExptReportVO exportGroupReport(@RequestParam String experimentInstanceId, @RequestParam String experimentGroupId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        boolean regenerate = regenerate(experimentInstanceId, accountId);
        return exptReportFacadeBiz.exportGroupReport(experimentInstanceId, experimentGroupId, accountId, regenerate, regenerate);
    }

    /**
     * 导出学生实验pdf报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出学生实验pdf报告
     * @date 2023/7/18 10:15
     */
    @Operation(summary = "导出学生实验pdf报告")
    @GetMapping(value = "/v1/report/exportAccountReport")
    public ExptReportVO exportAccountReport(@RequestParam String experimentInstanceId, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        boolean regenerate = regenerate(experimentInstanceId, accountId);
        return exptReportFacadeBiz.exportAccountReport(experimentInstanceId, accountId, regenerate, regenerate);
    }

    /**
     * 预览实验报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param request              -
     * @param response             -
     * @author fhb
     * @description 预览实验报告
     * @date 2023/7/21 14:35
     */
    @Operation(summary = "预览实验报告")
    @GetMapping("v1/report/previewExptReport")
    public void previewExptReport(@RequestParam String experimentInstanceId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) {
        String accountId = baseBiz.getAccountId(request);
        boolean regenerate = regenerate(experimentInstanceId, accountId);
        try {
            exptReportFacadeBiz.previewExptReport(experimentInstanceId, accountId, regenerate, regenerate, request, response);
        } catch (IOException e) {
            log.error(String.format("预览实验报告时，发生IO异常: %s", e));
            throw new BizException(String.format("预览实验报告时，发生IO异常: %s", e));
        }
    }

    /**
     * 预览小组报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId    - 实验小组ID
     * @param request              -
     * @param response             -
     * @author fhb
     * @description 预览小组报告
     * @date 2023/7/21 14:37
     */
    @Operation(summary = "预览小组报告")
    @GetMapping("v1/report/previewGroupReport")
    public void previewGroupReport(@RequestParam String experimentInstanceId,
                                   @RequestParam String experimentGroupId,
                                   @RequestParam String accountId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        boolean regenerate = regenerate(experimentInstanceId, accountId);
//        String accountId = baseBiz.getAccountId(request);
        try {
            exptReportFacadeBiz.previewGroupReport(experimentInstanceId, experimentGroupId, accountId, regenerate, regenerate, request, response);
        } catch (IOException e) {
            log.error(String.format("预览小组报告时，发生IO异常: %s", e));
            throw new BizException(String.format("预览小组报告时，发生IO异常: %s", e));
        }
    }

    /**
     * 预览学生报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param request              -
     * @param response             -
     * @author fhb
     * @description 预览学生报告
     * @date 2023/7/21 14:38
     */
    @Operation(summary = "预览学生报告")
    @GetMapping("v1/report/previewAccountReport")
    public void previewAccountReport(@RequestParam String experimentInstanceId,
                                     @RequestParam String accountId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
//        String accountId = baseBiz.getAccountId(request);
        boolean regenerate = regenerate(experimentInstanceId, accountId);
        try {
            exptReportFacadeBiz.previewAccountReport(experimentInstanceId, accountId, regenerate, regenerate, request, response);
        } catch (IOException e) {
            log.error(String.format("预览学生报告时，发生IO异常: %s", e));
            throw new BizException(String.format("预览学生报告时，发生IO异常: %s", e));
        }
    }

    /**
     * 获取实验报告数据
     *
     * @param exptInstanceId - 实验示例ID
     * @return org.dows.hep.vo.report.ExptReportModel
     * @author fhb
     * @description 获取实验报告数据
     * @date 2023/9/6 16:40
     */
    @Operation(summary = "获取实验报告数据")
    @GetMapping("v1/report/getExptPdfData")
    public ExptReportModel getExptPdfData(@RequestParam String exptInstanceId) {
        return exptReportFacadeBiz.getExptPdfData(exptInstanceId);
    }

    /**
     * 获取小组报告数据
     *
     * @param exptInstanceId - 实验示例ID
     * @param exptGroupId    - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportModel
     * @author fhb
     * @description 获取小组报告数据
     * @date 2023/9/6 16:41
     */
    @Operation(summary = "获取小组报告数据")
    @GetMapping("v1/report/getGroupPdfData")
    public ExptReportModel getGroupPdfData(@RequestParam String exptInstanceId,
                                           @RequestParam String exptGroupId) {
        return exptReportFacadeBiz.getGroupPdfData(exptInstanceId, exptGroupId);
    }

    /**
     * 获取个人报告数据
     *
     * @param exptInstanceId - 实验示例ID
     * @param accountId      - 账号ID
     * @return org.dows.hep.vo.report.ExptReportModel
     * @author fhb
     * @description 获取个人报告数据
     * @date 2023/9/6 16:43
     */
    @Operation(summary = "获取个人报告数据")
    @GetMapping("v1/report/getAccountPdfData")
    public ExptReportModel getAccountPdfData(@RequestParam String exptInstanceId,
                                             @RequestParam String accountId,
                                             HttpServletRequest request) {
//        accountId = baseBiz.getAccountId(request);
        return exptReportFacadeBiz.getAccountPdfData(exptInstanceId, accountId);
    }

    /**
     * 根据提供的页面，提供的功能点实现...指定操作
     *
     * @param func - 功能点
     * @param url  - 页面路径
     * @return java.lang.String
     * @author fhb
     * @description 根据提供的页面，提供的功能点实现...指定操作
     * @date 2023/9/6 17:18
     */
    @Operation(summary = "根据提供的页面，提供的功能点实现...指定操作")
    @GetMapping("v1/report/exportLooseCoupling")
    public String exportLooseCoupling(@RequestParam(defaultValue = "export") String func,
                                      @RequestParam(defaultValue = "hep") String appCode,
                                      @RequestParam String url) {
        Map<String, Object> param = new HashMap<>();
        param.put("fun", func);
        param.put("appCode", appCode);
        param.put("url", url);
        // "http://192.168.1.60:10004/pdf"
        String serverUrl = pdfServerProperties.getUrl();
        return HttpUtil.get(serverUrl, param);
    }

    private boolean regenerate(String experimentInstanceId) {
        return Boolean.FALSE;
    }

    // todo 定时任务保证方案设计审核截止时间后重新生成一份报告
    private boolean regenerate(String exptInstanceId, String accountId) {
        // 管理员 && 方案设计模式 --> 在审核截止前重新生成
        boolean isAdmin = baseBiz.isAdministrator(accountId);
        if (isAdmin) {
            // 获取实验模式
            ExptSettingModeEnum exptSettingMode = experimentSettingBiz.getExptSettingMode(exptInstanceId);
            if (exptSettingMode == null) {
                throw new BizException("下载报告时，获取实验设置数据异常");
            }

            // 不是方案设计 --> 不重新生成
            if (!ExptSettingModeEnum.SCHEME.equals(exptSettingMode)) {
                return Boolean.FALSE;
            }

            // 是方案设计
            ExperimentSetting.SchemeSetting schemeSetting = experimentSettingBiz.getSchemeSetting(exptInstanceId);
            // 审核截止时间
            long auditEndTime = Optional.of(schemeSetting)
                    .map(ExperimentSetting.SchemeSetting::getAuditEndTime)
                    .map(Date::getTime)
                    .orElseThrow(() -> new BizException("下载报告时，获取方案设计设置数据异常"));
            long current = DateUtil.current();
            // 未到审核截止时间
            if (current < auditEndTime) {
                return Boolean.TRUE;
            }
        }

        // 非管理员不重新生成
        return Boolean.FALSE;
    }


}
