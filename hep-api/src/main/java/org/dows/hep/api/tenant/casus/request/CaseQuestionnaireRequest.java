package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.QuestionTypeEnum;
import org.dows.hep.api.tenant.casus.CasePeriodEnum;
import org.dows.hep.api.tenant.casus.CaseQuestionSelectModeEnum;

import java.util.List;
import java.util.Map;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CaseQuestionnaire 对象", title = "案例问卷Request")
public class CaseQuestionnaireRequest{

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "试卷名称")
    private String questionSectionName;

    @Schema(title = "答题位置-期数")
    private CasePeriodEnum periods;

    @Schema(title = "添加方式")
    @NotNull(message = "添加方式不能为空")
    private CaseQuestionSelectModeEnum addType;

    @Schema(title = "随机添加方式")
    private List<RandomMode> randomModeList;

    @Schema(title = "手动添加方式")
    private List<String> questionInstanceIdList;

    @Data
    @NoArgsConstructor
    @Schema(name = "RandomMode 对象", title = "随机添加方式Request")
    public static class RandomMode {
        @Schema(title = "知识体系")
        private String l1CategId;

        @Schema(title = "知识类别")
        private String l2CategId;

        @Schema(title = "题目数量")
        private Map<QuestionTypeEnum, Integer> numMap;
    }

}
