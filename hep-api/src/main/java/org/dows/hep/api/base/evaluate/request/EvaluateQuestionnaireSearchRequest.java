package org.dows.hep.api.base.evaluate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/5 17:01
 */
@Data
@NoArgsConstructor
@Schema(name = "EvaluateQuestionnaireSearchRequest 对象", title = "评估问卷条件查询")
public class EvaluateQuestionnaireSearchRequest {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "类别ID")
    private List<String> categIds;
}
