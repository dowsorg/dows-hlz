package org.dows.hep.api.base.person.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(name = "PersonInstance 对象", title = "人物实例")
public class PersonInstanceResponse{
    @Schema(title = "姓名")
    private String name;

    @Schema(title = "头像")
    private String avatar;

    @Schema(title = "其他图示")
    private String extra;

    @Schema(title = "用户简介")
    private String intro;

    @Schema(title = "账号ID/用户ID/会员ID/商户ID")
    private String accountId;

    @Schema(title = "账号名")
    private String accountName;

    @Schema(title = "健康指数")
    private String healthPoint;

    @Schema(title = "用户名")
    private String userName;

    @Schema(title = "是否已发布")
    private Integer status;

    @Schema(title = "人物功能点集合")
    private List<CasePersonIndicatorFuncRequest> entityList;

    @Schema(title = "关键指标列表")
    private List<String> coreIndicators;
}
