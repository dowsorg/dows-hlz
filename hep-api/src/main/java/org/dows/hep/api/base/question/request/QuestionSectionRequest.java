package org.dows.hep.api.base.question.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.question.QuestionSectionAccessAuthEnum;
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
    private List<QuestionSectionItemRequest> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionRequest> questionSectionDimensionList;



    // JsonIgnore
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @JsonIgnore
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "来源")
    @JsonIgnore
    private String source;

    @Schema(title = "权限[000001]")
    @JsonIgnore
    private String permissions;

    @Schema(title = "题数")
    @JsonIgnore
    private Integer questionCount;

    @Schema(title = "题型结构")
    @JsonIgnore
    private String questionSectionStructure;

    @Schema(title = "引用计数")
    @JsonIgnore
    private Integer refCount;

    @Schema(title = "问题集标识")
    @JsonIgnore
    private String questionSectionIdentifier;

    @Schema(title = "版本号")
    @JsonIgnore
    private String ver;

    @Schema(title = "biz code")
    @JsonIgnore
    private QuestionSectionAccessAuthEnum bizCode;

}
