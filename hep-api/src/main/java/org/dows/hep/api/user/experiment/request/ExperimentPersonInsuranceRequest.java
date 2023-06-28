package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author jx
 * @date 2023/6/27 18:21
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentPersonInsurance 对象", title = "人物保险")
public class ExperimentPersonInsuranceRequest {

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "医疗占比")
    private BigDecimal per;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "购买保险机构")
    private String operateOrgId;

    @Schema(title = "报销比例")
    private Double reimburseRatio;

    @Schema(title = "保险生效时间")
    private Date indate;

    @Schema(title = "保险失效时间")
    private Date expdate;
}
