package org.dows.hep.api.base.indicator.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/17 23:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "FindJudgeGoal 对象", title = "查询条件")
public class FindJudgeGoalRequest {

    @Schema(title = "应用ID")
    @ApiModelProperty(required = true)
    private String appId;
    @Schema(title = "分页大小")
    @ApiModelProperty(required = true)
    private Integer pageSize;

    @Schema(title = "页码")
    @ApiModelProperty(required = true)
    private Integer pageNo;

    @Schema(title = "排序列表json")
    private String sorts;

    @Schema(title = "一级分类id")
    private List<String> categIdLv1;

    @Schema(title = "搜索关键字")
    private String keywords;

    @Schema(title = "包含的分布式id列表")
    private List<String> incIds;

    @Schema(title = "排除的分布式id列表")
    private List<String> excIds;

    @Schema(title = "状态 null-所有, 0-启用 1-停用")
    private Integer state;

}
