package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:54
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentSchemeResponse 对象", title = "实验方案设计")
public class ExperimentSchemeItemResponse {

    @Schema(title = "itemID")
    private String questionSectionItemId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "子 Item ")
    private List<ExperimentSchemeItemResponse> children;

}
