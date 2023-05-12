package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionSearch 对象", title = "问题无分页查询")
public class QuestionSearchRequest{
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "题型")
    private String questionType;

    @Schema(title = "类别ID集合")
    private List<String> categIdList;
}
