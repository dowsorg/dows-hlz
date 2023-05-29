package org.dows.hep.api.base.indicator.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.view.IndicatorCategoryResponseView;

import java.io.Serializable;
import java.util.Date;

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
@Schema(name = "IndicatorCategory对象", title = "指标类别列表")
public class IndicatorCategoryResponse implements Serializable {
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(title = "主键")
  private Long id;

  @JsonView(IndicatorCategoryResponseView.Public.class)
  @Schema(title = "指标类别分布式ID")
  private String indicatorCategoryId;

  @Schema(title = "应用ID")
  private String appId;

  @Schema(title = "父ID")
  private String pid;

  @JsonView(IndicatorCategoryResponseView.Public.class)
  @Schema(title = "分类名称")
  private String categoryName;

  @JsonView(IndicatorCategoryResponseView.Public.class)
  @Schema(title = "展示顺序")
  private Integer seq;

  @Schema(title = "逻辑删除")
  private Boolean deleted;

  @Schema(title = "时间戳")
  private Date dt;
}
