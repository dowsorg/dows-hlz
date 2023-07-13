package org.dows.hep.api.tenant.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/6/15 21:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExperimentSchemeScoreRequest 对象", title = "评分表")
public class ExperimentSchemeScoreRequest {

    @Schema(title = "方案设计-得分列表")
    private List<SchemeScoreRequest> scoreInfos;

    @Data
    public static class SchemeScoreRequest {
        @Schema(title = "方案设计评分ID")
        private String experimentSchemeScoreId;

        @Schema(title = "itemList")
        private List<SchemeScoreItemRequest> itemList;
    }

    @Data
    public static class SchemeScoreItemRequest {
        @Schema(title = "方案设计评分ItemId")
        private String experimentSchemeScoreItemId;

        @Schema(title = "最终得分")
        private Float score;
    }
}
