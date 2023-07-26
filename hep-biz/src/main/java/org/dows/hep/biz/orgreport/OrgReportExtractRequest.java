package org.dows.hep.biz.orgreport;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/7/18 11:34
 */
@Data
@Accessors(chain = true)
public class OrgReportExtractRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    @ApiModelProperty(required = true)
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    @ApiModelProperty(required = true)
    private String experimentOrgId;


    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "挂号流水ID")
    private String operateFlowId;

    @Schema(title = "当前期数")
    private Integer period;

}
