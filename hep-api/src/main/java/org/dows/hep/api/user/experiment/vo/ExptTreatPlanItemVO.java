package org.dows.hep.api.user.experiment.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/4 17:40
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptTreatPlanItemVO 对象", title = "治疗方案关联项目")
public class ExptTreatPlanItemVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "序号,新增为空,非空该条不可删除, 前端按该值倒序排列")
    private Long itemId;
    @Schema(title = "治疗项目ID")
    private String treatItemId;

    @Schema(title = "治疗项目名称")
    private String treatItemName;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "一级分类ID")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private String fee;

    @Schema(title = "用量")
    private String weight;

    @Schema(title = "结算标记 0-未结算 1-已结算,后端专用")
    private Integer dealFlag;



}
