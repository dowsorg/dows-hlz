package org.dows.hep.api.user.organization.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "AccountOrgGeo 对象", title = "机构经纬度")
public class AccountOrgGeoResponse{
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "机构ID")
    private String orgId;

    @Schema(title = "机构名称")
    private String orgName;

    @Schema(title = "组织机构经度")
    private BigDecimal orgLongitude;

    @Schema(title = "组织机构纬度")
    private BigDecimal orgLatitude;


}
