package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.user.experiment.vo.ExptJudgeGoalItemVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/10/19 11:03
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SaveJudgeGoal 对象", title = "学生管理目标")

public class ExptJudgeGoalResponse {

    @Schema(title = "管理目标列表")
    private List<ExptJudgeGoalItemVO> goalItems;

}
