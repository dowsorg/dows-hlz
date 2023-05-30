package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.BaseExptRequest;

/**
 * @author : wuzl
 * @date : 2023/5/30 14:48
 */
@Data
@NoArgsConstructor
@Schema(name = "FindInterveneCateg4Expt 对象", title = "查询条件")

public class FindInterveneCateg4ExptRequest extends BaseExptRequest {

    @Schema(title = "治疗项目根类别 [treat.item:指标功能点id] ")
    private String family;

    @Schema(title = "父类别")
    private String pid;

    @Schema(title = "是否包含子节点 0-否 1-是")
    private Integer withChild;
}
