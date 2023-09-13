package org.dows.hep.api.edw.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/9/12 17:17
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "HepOperateCostGetRequest 对象", title = "健康操作花费get请求")
public class HepOperateCostGetRequest {
    /**
     * 实验ID
     */
    @Schema(title = "实验ID")
    private Long experimentInstanceId;

    /**
     * 实验小组ID
     */
    @Schema(title = "实验小组ID")
    private Long experimentGroupId;

    /**
     * 操作者ID[小组成员]
     */
    @Schema(title = "操作者ID[小组成员]")
    private Long operatorId;

    /**
     * 实验人物ID[患者|居委主任|消防员]
     */
    @Schema(title = "实验人物ID[患者|居委主任|消防员]")
    private Long personId;

    /**
     * 机构树ID
     */
    @Schema(title = "机构树ID")
    private Long orgTreeId;

    /**
     * 流程Id[挂号ID|就诊ID]
     */
    @Schema(title = "流程Id[挂号ID|就诊ID]")
    private String flowId;
}
