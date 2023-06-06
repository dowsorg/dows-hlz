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

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验操作流程id")
    private String operateFlowId;

    @Schema(title = "指标功能点id")
    private String indicatorFuncId;

    @Schema(title = "机构功能类型 4-体格检查 5-辅助检查")
    private Integer funcType;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    private String experimentOrgId;

    @Schema(title = "体格检查分布式ID")
    private String indicatorViewPhysicalExamId;

    @Schema(title = "辅助检查分布式ID")
    private String indicatorViewSupportExamId;

    @Schema(title = "功能类型  1-基本信息 2-设置随访  3-开始随访 4-一般检查 11-健康问题 12-健康指导 13-疾病问题 14-健管目标 21-饮食干预 22-运动干预  23-自定义干预")
    private Integer operateType;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "机构操作id,非空时仅以此查询")
    private String operateOrgFuncId;

    @Schema(title = "分数")
    private String score;

    @Schema(title = "输入记录")
    private String inputJson;

}
