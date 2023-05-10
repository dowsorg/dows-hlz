package org.dows.hep.api.user.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "TransferPersonel 对象", title = "人物转移")
public class TransferPersonelRequest {
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "转移说明")
    private String descr;

    @Schema(title = "账户ID")
    private String accountId;

    @Schema(title = "机构ID")
    private String orgId;

}
