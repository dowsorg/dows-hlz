package org.dows.hep.biz.event.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/6/19 14:19
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class ExperimentCacheKey {

    @Schema(title = "应用id")
    private String appId;
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    public static ExperimentCacheKey create(String appId,String experimentInstanceId){
        return new ExperimentCacheKey(appId,experimentInstanceId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentCacheKey that = (ExperimentCacheKey) o;
        return Objects.equals(appId, that.appId) && Objects.equals(experimentInstanceId, that.experimentInstanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appId, experimentInstanceId);
    }

    @Override
    public String toString() {
        return String.format("%s-%s", appId,experimentInstanceId);
    }
}
