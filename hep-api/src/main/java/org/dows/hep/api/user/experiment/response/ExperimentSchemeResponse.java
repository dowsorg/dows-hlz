package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:53
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentSchemeResponse 对象", title = "实验方案设计")
public class ExperimentSchemeResponse {
    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "方案提示")
    private String schemeTips;

    @Schema(title = "方案说明")
    private String schemeDescr;

    @Schema(title = "是否包含视频")
    private Integer containsVideo;

    @Schema(title = "视频题干")
    private String videoQuestion;

    @Schema(title = "方案设计试卷")
    private List<ExperimentSchemeItemResponse> itemList;
}
