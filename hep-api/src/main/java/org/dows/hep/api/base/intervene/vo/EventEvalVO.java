package org.dows.hep.api.base.intervene.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/5/15 16:18
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "EventEvalVO 对象", title = "事件触发条件")
public class EventEvalVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "关联分布式id，删除使用")
    private String refId;

    @Schema(title = "表达式")
    private String expression;

    @Schema(title = "触发条件描述")
    private String expressionDescr;

}
