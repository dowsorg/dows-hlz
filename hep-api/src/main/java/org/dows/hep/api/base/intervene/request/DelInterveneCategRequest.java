package org.dows.hep.api.base.intervene.request;

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
@Schema(name = "DelInterveneCateg 对象", title = "删除类别")
public class DelInterveneCategRequest {
    @Schema(title = "分布式id列表")
    private String ids;


}
