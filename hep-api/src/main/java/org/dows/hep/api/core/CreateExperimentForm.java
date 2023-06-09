package org.dows.hep.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.account.response.AccountInstanceResponse;
import org.dows.hep.api.tenant.experiment.request.ExperimentSetting;

import java.util.Date;
import java.util.List;

/**
 * @description
 *
 * @author
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CreateExperiment 对象", title = "创建实验对象")
public class CreateExperimentForm {
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例实验ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "案例ID")
    private String caseOrgId;

    @Schema(title = "案例名称")
    private String caseName;

    @Schema(title = "实验名称")
    private String experimentName;

    @Schema(title = "实验说明")
    private String experimentDescr;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "参与教师")
    private List<AccountInstanceResponse> teachers;

    @Schema(title = "实验设置JSON对象")
    private ExperimentSetting experimentSetting;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "期数")
    private Integer periods;
}
