package org.dows.hep.api.base.intervene.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "DelRefItem 对象", title = "删除关联子项")
public class DelRefItemRequest {
    @Schema(title = "分布式refId列表")
    private List<String> ids;


}
