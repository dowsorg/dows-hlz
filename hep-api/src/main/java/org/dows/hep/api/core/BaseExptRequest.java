package org.dows.hep.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生端请求基类
 * @author : wuzl
 * @date : 2023/5/29 16:19
 */
@Data
@NoArgsConstructor
@Schema(name = "BaseExptRequest 对象", title = "学生端请求基类")
public class BaseExptRequest extends BasePageRequest {

    @Schema(title = "实验实例ID")
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例人物ID")
    private String caseAccountId;

    @Schema(title = "案例人物名称")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

}
