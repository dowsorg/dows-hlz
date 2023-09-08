package org.dows.hep.biz.eval.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Objects;

/**
 * @author : wuzl
 * @date : 2023/9/6 13:40
 */
@Data
@Accessors(chain = true)
public class EvalPersonOnceCacheKey {
    public EvalPersonOnceCacheKey(String experimentInstanceId, String experimentPersonId){
        this.experimentInstanceId=experimentInstanceId;
        this.experimentPersonId=experimentPersonId;
    }
    private static final int HASHNum=3;

    @Schema(title = "实验id")
    private String experimentInstanceId;

    @Schema(title = "实验人物id")
    private final String experimentPersonId;

    @Schema(title = "计算批次")
    private Integer evalNo;

    private int getEvalHash(){
        if(null==evalNo){
            return 0;
        }
        return evalNo%HASHNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EvalPersonOnceCacheKey that = (EvalPersonOnceCacheKey) o;

        if (!Objects.equals(experimentPersonId, that.experimentPersonId))
            return false;
        return Objects.equals(getEvalHash(), that.getEvalHash());
    }

    @Override
    public int hashCode() {
        return Objects.hash(experimentPersonId, getEvalHash());
    }

    public String getKeyString(){
        return String.format("%s-%s", experimentPersonId,getEvalHash());
    }

    @Override
    public String toString() {
        return String.format("%s-%s-%s-%s",experimentInstanceId, experimentPersonId,evalNo, getEvalHash());
    }

}
