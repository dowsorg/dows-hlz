package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 方案设计状态
 * @date 2023/6/27 13:54
 **/
@Data
@NoArgsConstructor
@Schema(name = "ExperimentSchemeStateResponse 对象", title = "实验方案设计状态")
public class ExperimentSchemeStateResponse {

    @Schema(title = "方案设计状态码")
    private Integer stateCode;

    @Schema(title = "方案设计状态描述")
    private String stateDescr;
}
