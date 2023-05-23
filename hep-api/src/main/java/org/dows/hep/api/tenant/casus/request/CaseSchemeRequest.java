package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
    private String schemeName;

    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "状态[0-关闭|1-启用]")
    private Integer enabled;

    @Schema(title = "方案提示")
    private String tips;

    @Schema(title = "方案说明")
    private String schemeDescr;

    @Schema(title = "添加方式")
    private String addType;

    @Schema(title = "是否包含视频[0-否|1-是]")
    private Integer containsVideo;

    @Schema(title = "视频问题题干")
    private String videoQuestion;

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
