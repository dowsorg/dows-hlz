package org.dows.hep.api.base.evaluate.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/23 17:53
 */
@Data
@NoArgsConstructor
@Schema(name = "EvaluateQuestionnairePageRequest 对象", title = "创建评估问卷")
public class EvaluateQuestionnairePageRequest {
    @Schema(title = "pageNo")
    private Long pageNo;

    @Schema(title = "pageSize")
    private Long pageSize;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "类别ID")
    private List<String> categIds;

}
