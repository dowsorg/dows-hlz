package org.dows.hep.rest.report;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.tenant.experiment.request.ExptAccountReportRequest;
import org.dows.hep.api.tenant.experiment.request.ExptGroupReportPageRequest;
import org.dows.hep.api.tenant.experiment.request.ExptReportPageRequest;
import org.dows.hep.api.tenant.experiment.response.ExptAccountReportResponse;
import org.dows.hep.api.tenant.experiment.response.ExptGroupReportPageResponse;
import org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse;
import org.dows.hep.biz.report.ExptReportFacadeBiz;
import org.dows.hep.biz.user.experiment.ExperimentBaseBiz;
import org.dows.hep.vo.report.ExptReportVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author fhb
 * @version 1.0
 * @folder report
 * @description 报告管理-pdf形式
 * @date 2023/7/6 16:38
 **/
@RequiredArgsConstructor
@RestController
@Tag(name = "pdf报告管理", description = "pdf报告管理")
public class ExptReportPdfRest {
    private final ExptReportFacadeBiz exptReportFacadeBiz;
    private final ExperimentBaseBiz baseBiz;

    /**
     *
     * 分页获取报告列表
     *
     * @param pageRequest - 分页请求
     * @param request - servletRequest
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptReportPageResponse>
     * @author fhb
     * @description 分页获取报告列表
     * @date 2023/7/31 16:15
     */
    @Operation(summary = "分页获取报告列表")
    @PostMapping(value = "/v1/report/pageExptReport")
    public IPage<ExptReportPageResponse> pageExptReport(ExptReportPageRequest pageRequest, HttpServletRequest request) {
        String accountId = baseBiz.getAccountId(request);
        return exptReportFacadeBiz.pageExptReport(pageRequest, accountId);
    }

    /**
     *
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
    public IPage<ExptGroupReportPageResponse> pageGroupReport(ExptGroupReportPageRequest pageRequest) {
        return exptReportFacadeBiz.pageGroupReport(pageRequest);
    }

    /**
     *
     * 分页获取学生报告列表
     *
     * @param pageRequest - 分页请求
     * @param request - servletRequest
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.dows.hep.api.tenant.experiment.response.ExptAccountReportResponse>
     * @author fhb
     * @description 分页获取学生报告列表
     * @date 2023/7/31 16:17
     */
    @Operation(summary = "分页获取学生报告列表")
    @PostMapping(value = "/v1/report/pageAccountReport")
    public IPage<ExptAccountReportResponse> pageAccountReport(ExptAccountReportRequest pageRequest, HttpServletRequest request) {
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
    public ExptReportVO exportExptReport(@RequestParam String experimentInstanceId) {
        return exptReportFacadeBiz.exportExptReport(experimentInstanceId);
    }

    /**
     * 导出小组实验pdf报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId - 实验小组ID
     * @return org.dows.hep.vo.report.ExptReportVO
     * @author fhb
     * @description 导出小组实验pdf报告
     * @date 2023/7/18 10:15
     */
    @Operation(summary = "导出小组实验pdf报告")
    @GetMapping(value = "/v1/report/exportGroupReport")
    public ExptReportVO exportGroupReport(@RequestParam String experimentInstanceId, @RequestParam String experimentGroupId) {
        return exptReportFacadeBiz.exportGroupReport(experimentInstanceId, experimentGroupId);
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
        return exptReportFacadeBiz.exportAccountReport(experimentInstanceId, accountId);
    }

    /**
     * 预览实验报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param request -
     * @param response -
     * @author fhb
     * @description 预览实验报告
     * @date 2023/7/21 14:35
     */
    @Operation(summary = "预览实验报告")
    @GetMapping("v1/report/previewExptReport")
    public void previewExptReport(@RequestParam String experimentInstanceId,
                                  HttpServletRequest request,
                                  HttpServletResponse response) throws IOException {
        exptReportFacadeBiz.previewExptReport(experimentInstanceId, request, response);
    }

    /**
     * 预览小组报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param experimentGroupId - 实验小组ID
     * @param request -
     * @param response -
     * @author fhb
     * @description 预览小组报告
     * @date 2023/7/21 14:37
     */
    @Operation(summary = "预览小组报告")
    @GetMapping("v1/report/previewGroupReport")
    public void previewGroupReport(@RequestParam String experimentInstanceId,
                                   @RequestParam String experimentGroupId,
                                   HttpServletRequest request,
                                   HttpServletResponse response) throws IOException {
        exptReportFacadeBiz.previewGroupReport(experimentInstanceId, experimentGroupId, request, response);
    }

    /**
     * 预览学生报告
     *
     * @param experimentInstanceId - 实验实例ID
     * @param request -
     * @param response -
     * @author fhb
     * @description 预览学生报告
     * @date 2023/7/21 14:38
     */
    @Operation(summary = "预览学生报告")
    @GetMapping("v1/report/previewAccountReport")
    public void previewAccountReport(@RequestParam String experimentInstanceId,
                                     HttpServletRequest request,
                                     HttpServletResponse response) {
        String accountId = baseBiz.getAccountId(request);
        exptReportFacadeBiz.previewAccountReport(experimentInstanceId, accountId, request, response);
    }
}
