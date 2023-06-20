package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.question.QuestionSectionGenerationModeEnum;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionSectionRequest 对象", title = "问题集Request")
public class QuestionSectionRequest{
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

    @Schema(title = "问卷生成模式[SELECT_CLONE ：从数据库选择并克隆一份 | SELECT_REF : 从数据库选择并引用 | ADD_NEW ： 添加新的]")
    private QuestionSectionGenerationModeEnum generationMode;

    @Schema(title = "问题集合")
    private List<QuestionSectionItemRequest> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionRequest> questionSectionDimensionList;

    @Schema(title = "创建者账号ID")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者Name")
    @JsonIgnore
    private String accountName;

}
