package org.dows.hep.biz.event.followupplan;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;
import org.ehcache.core.collections.ConcurrentWeakIdentityHashMap;

import java.util.concurrent.ConcurrentMap;

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
    private final ConcurrentMap<String,FollowupPlanRow> mapPlanRows=new ConcurrentWeakIdentityHashMap<>();
}
