package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.account.response.AccountGroupResponse;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/16 10:42
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "CaseAccountGroupResponse 对象", title = "案例机构人物")
public class CaseAccountGroupResponse extends AccountGroupResponse {

    @Schema(title = "关键指标列表")
    private List<String> coreIndicators;

}
