package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.BaseExptRequest;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "FindOrgNotice 对象", title = "查询条件")
public class FindOrgNoticeRequest extends BaseExptRequest {

    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;


}
