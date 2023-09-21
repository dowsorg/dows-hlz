package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperimentPhysicalExamCheckRequestRs implements Serializable {
  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "实验id")
  private String experimentId;

  @Schema(title = "期数")
  private Integer periods;

  @Schema(title = "实验人物id")
  private String experimentPersonId;

  @Schema(title = "功能点id")
  private String indicatorFuncId;

  @Schema(title = "机构id")
  private String experimentOrgId;

  @Schema(title = "体格检查id")
  private List<String> experimentIndicatorViewPhysicalExamIdList;
  /**
   * 存mongodb新增字段
   */
  @Schema(title = "实验分组id")
  private String experimentGroupId;
  @Schema(title = "流程id")
  private String operateFlowId;
  @Schema(title = "机构名称")
  private String orgName;
  @Schema(title = "(职责|能力|功能|菜单)名称[体格检查报告,辅助检查报告......]")
  private String functionName;
/*  @Schema(title = "json辅助检查明细数据")
  private String data;*/
  @Schema(title = "所在天数")
  private Integer onDay;
  @Schema(title = "检查时间(仿真时间)")
  private Date onDate;
}
