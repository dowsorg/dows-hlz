package org.dows.hep.api.base.person.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.tenant.casus.request.CasePersonIndicatorFuncRequest;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "PersonInstance 对象", title = "人物-实例")
public class PersonInstanceRequest{

    @Schema(title = "账户ID")
    private Long accountId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "姓名")
    private String name;

    @Schema(title = "状态")
    private Integer status;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "用户简介")
    private String intro;

    @Schema(title = "人物功能点集合")
    private List<CasePersonIndicatorFuncRequest> entityList;
}
