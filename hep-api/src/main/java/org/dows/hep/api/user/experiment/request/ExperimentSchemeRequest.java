package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/7 13:47
 */
@Data
@RequiredArgsConstructor
@Schema(name = "ExperimentSchemeRequest 对象", title = "实验方案设计")
public class ExperimentSchemeRequest {
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "方案设计试卷")
    private List<ExperimentSchemeItemRequest> itemList;
}
