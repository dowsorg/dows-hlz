package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseSchemeSearch 对象", title = "案例方案搜索")
public class CaseSchemeSearchRequest{

    @Schema(title = "appId")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "类别ID")
    private List<String> categIds;

    @Schema(title = "关键字")
    private String keyword;

    @Schema(title = "来源")
    private String source;


}
