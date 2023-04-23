package org.dows.hep.api.user.experiment.request;

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
@Schema(name = "FindOrgNotice 对象", title = "查询条件")
public class FindOrgNoticeRequest{
    @Schema(title = "分页大小")
    private Integer pageSize;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "案例机构id")
    private String caseOrgId;

    @Schema(title = "期数")
    private Integer periods;


}
