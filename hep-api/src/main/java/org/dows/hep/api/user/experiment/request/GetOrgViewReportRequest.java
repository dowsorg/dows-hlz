package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "GetOrgViewReport 对象", title = "查询条件")
public class GetOrgViewReportRequest{
    @Schema(title = "机构功能类型 4-体格检查 5-辅助检查")
    private Integer funcType;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构功能ID")
    private String caseOrgFunctionId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "机构操作id,非空时仅以此查询")
    private String operateOrgFuncId;

    @Schema(title = "0-获取所有 1-只获取输入 2-只获取报告")
    private Integer actFlag;


}
