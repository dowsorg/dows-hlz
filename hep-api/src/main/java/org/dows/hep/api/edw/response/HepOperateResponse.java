package org.dows.hep.api.edw.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.edw.HepOperateTypeEnum;

import java.util.Date;

/**
 * @author fhb
 * @version 1.0
 * @description 操作记录-response
 * @date 2023/9/12 15:19
 **/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "HepOperateResponse 对象", title = "操作记录相应")
public class HepOperateResponse {
    /**
     * 操作类型
     */
    @Schema(title = "操作类型")
    private HepOperateTypeEnum type;

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

    /**
     * 机构名称
     */
    @Schema(title = "机构名称")
    private String orgName;

    /**
     * (职责|能力|功能|菜单)名称[体格检查报告,辅助检查报告......]
     */
    @Schema(title = "(职责|能力|功能|菜单)名称[体格检查报告,辅助检查报告......]")
    private String functionName;

    /**
     * (职责|能力|功能|菜单|指标)code
     */
    @Schema(title = "(职责|能力|功能|菜单|指标)code")
    private String functionCode;

    /**
     * 记录值json数组
     */
    @Schema(title = "记录值json数组")
    private String data;

    /**
     * 期数
     */
    @Schema(title = "期数")
    private Integer period;

    /**
     * 所在天数
     */
    @Schema(title = "所在天数")
    private Integer onDay;

    /**
     * 序号
     */
    @Schema(title = "序号")
    private Integer seq;

    /**
     * 检查时间(仿真时间)
     */
    @Schema(title = "检查时间(仿真时间)")
    private Date onDate;
}
