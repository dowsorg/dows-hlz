package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/9/6 11:35
 */

@Data
@Accessors(chain = true)
public class EvalPersonCacheKey {

    public EvalPersonCacheKey(String experimentInstanceId, String experimentPersonId){
        this.experimentInstanceId=experimentInstanceId;
        this.experimentPersonId=experimentPersonId;
    }

    @Schema(title = "实验id")
    private String experimentInstanceId;
    @Schema(title = "实验人物id")
    private final String experimentPersonId;

    @Schema(title = "期数")
    private Integer period;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvalPersonCacheKey that = (EvalPersonCacheKey) o;
        return Objects.equals(experimentPersonId, that.experimentPersonId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentPersonId);
    }
}
