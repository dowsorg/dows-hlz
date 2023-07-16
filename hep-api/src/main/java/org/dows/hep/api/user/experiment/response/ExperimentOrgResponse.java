package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.account.response.AccountOrgResponse;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "ExperimentOrgResponse 对象", title = "实验机构")
public class ExperimentOrgResponse extends AccountOrgResponse {

    @Schema(title = "操作手册")
    private String handbook;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "机构费用列表")
    private List<ExperimentOrgFeeResponse> feeList;


    @Data
    @Schema(name = "ExperimentOrgFeeResponse 对象", title = "实验机构费用")
    public static class ExperimentOrgFeeResponse {
        @Schema(title = "机构ID")
        private String caseOrgId;
        @Schema(title = "费用Code")
        private String feeCode;

        @Schema(title = "费用名称")
        private String feeName;

        @Schema(title = "费用")
        private Double fee;

        @Schema(title = "报销比例")
        private Double reimburseRatio;



    }
}
