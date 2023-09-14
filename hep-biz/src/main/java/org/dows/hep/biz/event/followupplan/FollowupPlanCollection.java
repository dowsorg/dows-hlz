package org.dows.hep.biz.event.followupplan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.biz.event.sysevent.data.SysEventRow;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/9/2 19:16
 */

@Data
@Accessors(chain = true)
public class FollowupPlanCollection {
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "随访计划列表")
    private List<FollowupPlanRow> planRows;
}
