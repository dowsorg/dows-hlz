package org.dows.hep.api.user.experiment.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/7/17 19:45
 */
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptOrgReportNodeVO 对象", title = "机构挂号报告节点")
public class ExptOrgReportNodeVO {


    @Schema(title = "功能点类别ID")
    private String indicatorCategoryId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "指标功能点名称")
    private String indicatorFuncName;

    @Schema(title = "节点数据")
    private ExptOrgReportNodeDataVO nodeData;
}
