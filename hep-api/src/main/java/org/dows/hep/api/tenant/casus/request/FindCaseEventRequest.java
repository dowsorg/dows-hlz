package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
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
@Schema(name = "FindEvent 对象", title = "查询条件")
public class FindCaseEventRequest {
    @Schema(title = "应用ID")
    private String appId;
    @Schema(title = "分页大小")
    private Integer pageSize;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "排序列表json")
    private String sorts;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "搜索关键字")
    private String keywords;

    @Schema(title = "包含的分布式id列表")
    private List<String> incIds;

    @Schema(title = "排除的分布式id列表")
    private List<String> excIds;

    @Schema(title = "状态 null-所有, 0-启用 1-停用")
    private Integer state;

    @Schema(title = "触发类型 null-所有 0-条件触发 1-第一期 2-第二期...5-第5期")
    private Integer triggerType;

    @Schema(title = "人物accountId 数据库/案例人物")
    @NotEmpty(message = "人物ID不可为空")
    private String personId;


}
