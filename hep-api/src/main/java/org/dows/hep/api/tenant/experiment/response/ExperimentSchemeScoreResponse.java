package org.dows.hep.api.tenant.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author fhb
 * @description
 * @date 2023/6/15 21:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentSchemeScoreResponse 对象", title = "评分表")
public class ExperimentSchemeScoreResponse {
    @Schema(title = "方案设计评分ID")
    private String experimentSchemeScoreId;

    @Schema(title = "评审账号ID")
    private String reviewAccountId;

    @Schema(title = "评审账号名")
    private String reviewAccountName;

    @Schema(title = "评审得分-每个评审人给的分数")
    private Float reviewScore;

    @Schema(title = "方案设计评分ItemList-新增使用")
    private List<ExperimentSchemeScoreItemResponse> itemList;

    @Schema(title = "方案设计评分ItemMap-展示使用")
    private Map<String, List<ExperimentSchemeScoreItemResponse>> itemMap;
}
