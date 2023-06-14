package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description 实验方案设计设置信息
 * @date 2023/6/13 17:16
 */
@Data
@NoArgsConstructor
@Schema(name = "ExperimentSchemeSettingResponse 对象", title = "实验方案设计设置信息")
public class ExperimentSchemeSettingResponse {

    @Schema(title = "截止时间-时间戳字符串形式")
    private String schemeEndTime;

    @Schema(title = "剩余时间-时间戳字符串形式")
    private String remainingTime;

}
