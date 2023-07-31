package org.dows.hep.api.tenant.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 实验小组报告分页响应
 * @date 2023/7/31 14:12
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptGroupReportPageResponse 对象", title = "实验小组报告分页响应")
public class ExptGroupReportPageResponse {
    @Schema(title = "实验小组ID")
    private String exptGroupId;

    @Schema(title = "实验小组名")
    private String exptGroupName;

    @Schema(title = "实验小组别名")
    private String exptGroupAlign;

    @Schema(title = "实验小组成员")
    private String exptGroupMembers;

    @Schema(title = "实验最终得分")
    private String totalScore;
}
