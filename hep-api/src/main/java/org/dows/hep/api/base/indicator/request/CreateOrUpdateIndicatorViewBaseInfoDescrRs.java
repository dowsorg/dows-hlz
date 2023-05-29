package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author runsix
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrUpdateIndicatorViewBaseInfoDescrRs implements Serializable {
  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoDescId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  @ApiModelProperty(required = true)
  private String indicatorViewBaseInfoId;

  @Schema(title = "指标基本信息描述表名称")
  @ApiModelProperty(required = true)
  private String name;

  @Schema(title = "展示顺序")
  @ApiModelProperty(required = true)
  private Integer seq;

  @ApiModelProperty(required = true, value = "基本信息-指标描述表与指标关联关系列表")
  private List<CreateOrUpdateIndicatorViewBaseInfoDescrRefRequestRs> createOrUpdateIndicatorViewBaseInfoDescrRefRequestRsList;
}