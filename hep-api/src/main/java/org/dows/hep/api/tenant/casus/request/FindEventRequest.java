package org.dows.hep.api.tenant.casus.request;

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
@Schema(name = "FindEvent 对象", title = "查询条件")
public class FindEventRequest{
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

    @Schema(title = "触发类型 1-事件触发 2-条件触发")
    private Integer triggerType;


}
