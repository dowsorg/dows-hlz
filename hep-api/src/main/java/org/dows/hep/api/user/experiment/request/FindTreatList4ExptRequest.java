package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/30 11:15
 */
@Data
@NoArgsConstructor
@Schema(name = "FindTreatList4Expt 对象", title = "查询条件")
public class FindTreatList4ExptRequest extends ExptOrgFuncRequest {

    @Schema(title = "包含的分布式id列表")
    private List<String> incIds;


}
