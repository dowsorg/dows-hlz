package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.request.QuestionSectionDimensionRequest;
import org.dows.hep.api.base.question.request.QuestionSectionItemRequest;

import java.util.List;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "CaseScheme 对象", title = "案例方案")
public class CaseSchemeRequest{
    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案名称")
    @NotBlank(message = "方案名称不能为空")
    @Size(min = 1, max = 30, message = "方案设计名称长度限制为 1-30")
    private String schemeName;

    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "状态[0-关闭|1-启用]")
    private Integer enabled;

    @Schema(title = "方案提示")
    @NotBlank(message = "方案提示不能为空")
    @Size(min = 1, max = 200, message = "方案提示内容长度限制为 1-200")
    private String tips;

    @Schema(title = "方案说明")
    @NotBlank(message = "方案说明不能为空")
    @Size(min = 1, max = 300, message = "方案说明内容长度限制为 1-300")
    private String schemeDescr;

    @Schema(title = "添加方式")
    private String addType;

    @Schema(title = "是否包含视频[0-否|1-是]")
    private Integer containsVideo;

    @Schema(title = "视频问题题干")
    private String videoQuestion;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "问题集合")
    @Valid
    private List<QuestionSectionItemRequest> sectionItemList;

    @Schema(title = "维度集合")
    @Valid
    private List<QuestionSectionDimensionRequest> questionSectionDimensionList;


    // JsonIgnore

    @Schema(title = "创建者账号ID")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者Name")
    @JsonIgnore
    private String accountName;
}
