package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/7/8 22:16
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptOrgEventActionVO 对象", title = "突发事件处理措施")
public class ExptOrgEventActionVO {

    @Schema(title = "事件处理措施id")
    private String caseEventActionId;

    @Schema(title = "处理措施描述")
    private String actionDesc;



}
