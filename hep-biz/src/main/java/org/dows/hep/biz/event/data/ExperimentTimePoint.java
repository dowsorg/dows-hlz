package org.dows.hep.biz.event.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.enums.EnumExperimentState;

import java.time.LocalDateTime;

/**
 * @author : wuzl
 * @date : 2023/6/21 11:35
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor

public class ExperimentTimePoint {
    @Schema(title = "期数")
    private Integer period;

    @Schema(title = "累计暂停秒数")
    private Long cntPauseSeconds;
    @Schema(title = "现实时间")
    private LocalDateTime realTime;
    @Schema(title = "游戏内天数")
    private Integer gameDay;

    @Schema(title = "游戏状态")
    private EnumExperimentState gameState;

}
