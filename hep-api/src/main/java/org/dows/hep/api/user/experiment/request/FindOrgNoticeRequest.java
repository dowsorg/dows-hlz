package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.BaseExptRequest;

import java.util.List;

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
    @ApiModelProperty(required = true)
    private String experimentOrgNoticeId;

    @JsonIgnore
    @Schema(title = "机构人物列表")
    private List<String> experimentPersonIds;


}
