package org.dows.hep.api.tenant.casus.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.base.question.response.QuestionSectionDimensionResponse;
import org.dows.hep.api.base.question.response.QuestionSectionItemResponse;

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
@Schema(name = "CaseScheme 对象", title = "案例方案Form")
public class CaseSchemeResponse{

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案名称")
    private String schemeName;

    @Schema(title = "类别ID")
    private String caseCategId;

    @Schema(title = "类别名")
    private String caseCategName;

    @Schema(title = "状态[0-启用|1-关闭]")
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
    private List<QuestionSectionItemResponse> sectionItemList;

    @Schema(title = "维度集合")
    private List<QuestionSectionDimensionResponse> questionSectionDimensionList;

    @Schema(title = "维度Map")
    private Map<String, List<QuestionSectionDimensionResponse>> questionSectionDimensionMap;

    @Schema(title = "来源[admin|tenant]")
    private String source;

    @Schema(title = "创建者账号ID")
    private String accountId;

    @Schema(title = "创建者Name")
    private String accountName;
}
