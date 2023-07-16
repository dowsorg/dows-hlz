package org.dows.hep.api.core;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 学生端请求基类
 * @author : wuzl
 * @date : 2023/5/29 16:19
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "BaseExptRequest 对象", title = "学生端请求基类")
public class BaseExptRequest extends BasePageRequest {

    @Schema(title = "实验实例ID")
    @ApiModelProperty(required = true)
    private String experimentInstanceId;
    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    @ApiModelProperty(required = true)
    private String experimentPersonId;

    @Schema(title = "实验机构ID")
    @ApiModelProperty(required = true)
    private String experimentOrgId;

    @Schema(title = "期数")
    private Integer periods;

}
