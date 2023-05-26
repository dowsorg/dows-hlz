package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "FindTreat 对象", title = "查询条件")
public class FindTreatRequest{

    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分页大小")
    private Integer pageSize;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "排序列表json")
    private String sorts;

    @Schema(title = "一级分类id")
    private List<String> categIdLv1;

    @Schema(title = "搜索关键字")
    private String keywords;

    @Schema(title = "功能点id")
    @NotNull(message = "功能点ID不可为空")
    private String indicatorFuncId;

    @Schema(title = "包含的分布式id列表")
    private List<String> incIds;

    @Schema(title = "排除的分布式id列表")
    private List<String> excIds;

    @Schema(title = "状态 null-所有, 0-启用 1-停用")
    private Integer state;


}
