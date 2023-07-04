package org.dows.hep.biz.snapshot;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/6/27 14:19
 */
@Data
@Accessors(chain = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SnapshotRequest {
    @Schema(title = "应用id")
    private String appId;
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Override
    public String toString() {
        return String.format("appId:%s experimentInstanceId:%s",appId,experimentInstanceId);
    }
}
