package org.dows.hep.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页基类
 * @author : wuzl
 * @date : 2023/5/29 16:16
 */
@Data
@NoArgsConstructor
@Schema(name = "BasePageRequest 对象", title = "分页基类")
public class BasePageRequest {
    @Schema(title = "应用id")
    private String appId;

    @Schema(title = "分页大小")
    private Integer pageSize;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "排序列")
    private String sortField;

    @Schema(title = "排序顺序 0-正序 1-倒序")
    private Integer sortDesc;


}
