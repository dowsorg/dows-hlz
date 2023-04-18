package org.dows.hep.api.base.question.request;

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
@Schema(name = "QuestionSectionResultSearch 对象", title = "关键字聚合")
public class QuestionSectionResultSearchRequest{
    @Schema(title = "页数")
    private Integer pageNo;

    @Schema(title = "页大小")
    private Integer pageSize;

    @Schema(title = "appId")
    private String appId;


}
