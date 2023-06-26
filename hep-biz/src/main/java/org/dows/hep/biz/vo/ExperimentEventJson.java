package org.dows.hep.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.CaseEventActionInfoVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/17 23:08
 */
@Data
@Builder
@Accessors(chain = true)
public class ExperimentEventJson {
    @Schema(title = "案例事件ID")
    private String caseEventId;
    @Schema(title = "案例事件名称")
    private String caseEventName;

    @Schema(title = "人物Id")
    private String personId;

    @Schema(title = "人物名称")
    private String personName;

    @Schema(title = "事件分类id")
    private String eventCategId;

    @Schema(title = "事件分类名称")
    private String categName;

    @Schema(title = "事件说明")
    private String descr;

    @Schema(title = "事件提示")
    private String tips;

    @Schema(title = "触发类型 0-条件触发 1-第一期 2-第二期...5-第5期")
    private Integer triggerType;

    @Schema(title = "触发时间段 1-前期 2-中期 3-后期")
    private String triggerSpan;

    @Schema(title = "事件处理措施列表")
    private List<CaseEventActionInfoVO> actions;
}
