package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionSearch 对象", title = "关键字聚合")
public class QuestionSearchRequest {
    @Schema(title = "应用ID")
    private String appId;


}
