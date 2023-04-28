package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@Builder
@Schema(name = "IndicatorFunc 对象", title = "指标功能列表")
public class IndicatorFuncResponse{
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "指标功能分布式ID")
  private String indicatorFuncId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "指标功能父类别分布式ID")
  private String pid;

  @Schema(title = "功能名称")
  private String name;

  @Schema(title = "操作提示")
  private String operationTip;

  @Schema(title = "对话提示")
  private String dialogTip;

  @Schema(title = "展示顺序")
  private Integer seq;
}
