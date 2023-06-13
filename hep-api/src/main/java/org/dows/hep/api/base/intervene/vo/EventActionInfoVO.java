package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.indicator.response.IndicatorExpressionResponseRs;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/15 16:19
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventActionInfoVO 对象", title = "事件处理措施")
public class EventActionInfoVO {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id,新增时为空")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "处理措施描述")
    private String actionDesc;


    @Schema(title = "处理措施影响指标列表(公式source=9)")
    private List<IndicatorExpressionResponseRs> actionExpresssions;
}
