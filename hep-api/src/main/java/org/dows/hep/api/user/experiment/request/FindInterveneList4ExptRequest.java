package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/5/30 11:15
 */
@Data
@NoArgsConstructor
@Schema(name = "FindInterveneList4Expt 对象", title = "查询条件")
public class FindInterveneList4ExptRequest extends ExptOrgFuncRequest {
    @Schema(title = "一级分类id")
    private List<String> categIdLv1;

    @Schema(title = "搜索关键字")
    private String keywords;

    @Schema(title = "包含的分布式id列表")
    private List<String> incIds;

    @Schema(title = "排除的分布式id列表")
    private List<String> excIds;

    @Schema(title = "状态 null-所有, 0-启用 1-停用")
    private Integer state;
}
