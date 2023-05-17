package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class IndicatorInstanceCategoryResponseRs implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @Schema(title = "指标类别分布式ID")
  private String indicatorCategoryId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "父ID")
  private String pid;

  @Schema(title = "分类名称")
  private String categoryName;

  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "时间戳")
  private Date dt;

  @Schema(title = "指标列表")
  private List<IndicatorInstanceResponseRs> indicatorInstanceResponseRsList;
}
