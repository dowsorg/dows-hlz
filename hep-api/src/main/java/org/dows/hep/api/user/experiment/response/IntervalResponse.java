package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.user.experiment.vo.ExptTimePointVO;

import java.util.Map;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "CountDown 对象", title = "时间")
public class IntervalResponse extends ExptTimePointVO {
    @Schema(title = "应用ID")
    private String appId;


    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;
    @Schema(title = "模式")
    private Integer model;











    /**
     * 用于前端计算
     */
    // 每期持续时长
    @Schema(title = "每期持续时长")
    private Map<String, Integer> durationMap;
    // 期数
    @Schema(title = "期数")
    private Map<String, Integer> periodMap;
    // 每期对应的mock比列
    @Schema(title = "每期对应的mock比列")
    private Map<String, Double> mockRateMap;


}
