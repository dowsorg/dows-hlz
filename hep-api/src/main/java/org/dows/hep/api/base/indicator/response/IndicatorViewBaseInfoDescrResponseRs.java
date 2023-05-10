package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class IndicatorViewBaseInfoDescrResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoDescId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "分布式ID")
  private String indicatorViewBaseInfoId;

  @Schema(title = "指标基本信息描述表名称")
  private String name;

  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "关联指标列表")
  private List<IndicatorViewBaseInfoDescrRefResponseRs> indicatorViewBaseInfoDescrRefResponseRsList;
}
