package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.BaseExptRequest;

/**
 * @author : wuzl
 * @date : 2023/5/30 11:15
 */
@Data
@NoArgsConstructor
@Schema(name = "GetInfo4Expt 对象", title = "获取详情")
public class GetInfo4ExptRequest extends BaseExptRequest {

    @Schema(title = "主体ID")
    private String instanceId;

}
