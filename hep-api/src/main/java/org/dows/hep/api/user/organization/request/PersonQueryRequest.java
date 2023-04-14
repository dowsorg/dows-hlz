package org.dows.hep.api.user.organization.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author
 * @description
 * @date
 */
@Data
@NoArgsConstructor
@Schema(name = "PersonQuery 对象", title = "关键字")
public class PersonQueryRequest {
    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "用户名")
    private String userName;

    @Schema(title = "标签")
    private String tagName;


}
