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
@Schema(name = "FindInterveneCateg 对象", title = "查询条件")
public class FindInterveneCategRequest {
    @Schema(title = "类别列表,逗号分隔")
    private String sections;

    @Schema(title = "父级categ_id")
    private String pid;


}
