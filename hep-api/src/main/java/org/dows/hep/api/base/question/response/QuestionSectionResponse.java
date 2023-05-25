package org.dows.hep.api.base.question.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.enums.QuestionSectionGenerationModeEnum;

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
@Schema(name = "QuestionSectionResponse 对象", title = "问题集Response")
public class QuestionSectionResponse {
    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "类别Id")
    private String questionSectionCategId;

    @Schema(title = "问题集名称")
    private String name;

    @Schema(title = "问题集提示")
    private String tips;

    @Schema(title = "问题集说明")
    private String descr;

    @Schema(title = "排序")
    private Integer sequence;

    @Schema(title = "状态")
    private Integer enabled;

    @Schema(title = "创建者账号Id")
    private String accountId;

    @Schema(title = "创建者姓名")
    private String accountName;

    @Schema(title = "问卷生成模式[SELECT ：从数据库选择 | ADD_NEW ： 添加新的]")
    private QuestionSectionGenerationModeEnum generationMode;

    @Schema(title = "问题集合")
    private List<QuestionSectionItemResponse> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionResponse> questionSectionDimensionList;

    @Schema(title = "维度Map")
    private Map<String, List<QuestionSectionDimensionResponse>> questionSectionDimensionMap;
}
