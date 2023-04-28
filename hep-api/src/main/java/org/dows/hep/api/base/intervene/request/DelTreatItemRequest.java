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
@Schema(name = "DelTreatItem 对象", title = "删除治疗项目")
public class DelTreatItemRequest{
    @Schema(title = "分布式id列表")
    private List<String> ids;


}
