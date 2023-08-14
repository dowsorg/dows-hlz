package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "FindOrgReport 对象", title = "查询条件")
public class FindOrgReportRequest extends ExptOrgFuncRequest {

    @JsonIgnore
    @Schema(title = "实验人物ID列表")
    private List<String> experimentPersonIds;
}
