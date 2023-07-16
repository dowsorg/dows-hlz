package org.dows.hep.api.tenant.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.tenant.experiment.response.ExperimentSchemeScoreResponse;
import org.dows.hep.api.user.experiment.response.ExperimentSchemeResponse;

import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验方案设计评分vo
 * @date 2023/7/12 19:12
 **/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptSchemeScoreReviewVO 对象", title = "评分VO表")
public class ExptSchemeScoreReviewVO {

    @Schema(title = "方案设计-记录信息")
    private SchemeRecordInfo schemeRecordInfo;

    @Schema(title = "方案设计-评分信息")
    private SchemeScoreInfo schemeScoreInfo;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemeRecordInfo {
        @Schema(title = "组名")
        private String groupName;

        @Schema(title = "组别名")
        private String groupAlias;

        @Schema(title = "方案设计信息")
        private ExperimentSchemeResponse schemeInfo;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SchemeScoreInfo {
        @Schema(title = "总得分-平均所有评审人后的最终得分")
        private Float finalScore;

        @Schema(title = "各个评分人给的评分信息")
        private List<ExperimentSchemeScoreResponse> scoreInfos;
    }
}
