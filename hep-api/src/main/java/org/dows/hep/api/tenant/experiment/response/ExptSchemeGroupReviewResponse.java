package org.dows.hep.api.tenant.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fhb
 * @version 1.0
 * @description 方案设计小组response
 * @date 2023/7/12 15:06
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptSchemeGroupResponse 对象", title = "方案设计评分小组信息")
public class ExptSchemeGroupReviewResponse {
    @Schema(title = "方案设计小组ID")
    private String exptGroupId;

    @Schema(title = "方案设计小组名称-[xxx队]")
    private String exptGroupName;

    @Schema(title = "方案设计小组别名-[第一组]")
    private String exptGroupAliasName;

    @Schema(title = "方案设计小组序号-[1、2、3]")
    private String groupNo;

    @Schema(title = "方案设计小组状态-code")
    private Integer exptSchemeStateCode;

    @Schema(title = "方案设计小组状态-name")
    private String exptSchemeStateName;

    @Schema(title = "评审时间-exptSchemeStateCode=2时展示")
    private Date reviewDt;

    @Schema(title = "方案设计小组得分")
    private Float reviewScore;

}
