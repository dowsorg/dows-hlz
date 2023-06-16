package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Schema(name = "CaseInstancePage 对象", title = "分页请求Request")
public class CaseInstancePageRequest{

    @Schema(title = "pageNo")
    private Long pageNo;

    @Schema(title = "pageSize")
    private Long pageSize;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "案例状态[0:未发布|1:发布]")
    private Integer state;

    @Schema(title = "账号ID")
    @JsonIgnore
    private String accountId;

}
