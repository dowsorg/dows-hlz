package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 获取人物电子档案的基本信息，根据功能点
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetIndicatorBaseInfo {
    @Schema(title = "实验ID")
    private String experimentId;
    @Schema(title = "机构uID")
    private String orgId;
    @Schema(title = "实验人物ID")
    private String experimentPersonId;
    @Schema(title = "期数")
    private Integer periods;
}
