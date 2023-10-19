package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.core.ExptOrgFuncRequest;
import org.dows.hep.api.user.experiment.vo.ExptJudgeGoalItemVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/19 10:35
 */

@Data
@NoArgsConstructor
@Schema(name = "SaveExptJudgeGoalRequest 对象", title = "保存学生管理目标")

public class SaveExptJudgeGoalRequest extends ExptOrgFuncRequest {
    @Schema(title = "管理目标列表")
    private List<ExptJudgeGoalItemVO> goalItems;
}
