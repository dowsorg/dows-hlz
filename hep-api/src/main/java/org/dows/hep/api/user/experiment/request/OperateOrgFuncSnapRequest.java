package org.dows.hep.api.user.experiment.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author jx
 * @date 2023/6/1 14:45
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Schema(name = "OperateOrgFuncSnap 对象", title = "学生机构操作快照")
public class OperateOrgFuncSnapRequest {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验操作流程快照id")
    private String operateOrgFuncSnapId;

    @Schema(title = "输入记录")
    private String inputJson;

    @Schema(title = "结果记录")
    private String resultJson;
}
