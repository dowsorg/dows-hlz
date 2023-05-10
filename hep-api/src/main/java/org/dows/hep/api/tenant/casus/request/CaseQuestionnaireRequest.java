package org.dows.hep.api.tenant.casus.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.dows.hep.api.tenant.casus.QuestionSelectModeEnum;

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
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例问卷ID")
    private String caseQuestionnaireId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "试卷名称")
    private String questionSectionName;

    @Schema(title = "答题位置-期数")
    private String periods;

    @Schema(title = "答题位置-期数排序")
    private Integer periodSequence;

    @Schema(title = "添加方式")
    private QuestionSelectModeEnum addType;

    @Schema(title = "随机添加方式")
    private List<RandomMode> randomModeList;

    @Schema(title = "手动添加方式")
    private List<String> questionInstanceIdList;

    @Data
    @NoArgsConstructor
    @Schema(name = "RandomMode 对象", title = "随机添加方式Request")
    public static class RandomMode {
        @Schema(title = "分类路径")
        private List<String> questionCategIdPaths;

        @Schema(title = "题目数量")
        private Map<String, Integer> numMap;

//        @Schema(title = "单选题-题目数量")
//        private Integer radioSelectNum;
//
//        @Schema(title = "单选题-题目数量")
//        private Integer multipleSelectNum;
//
//        @Schema(title = "材料题-题目数量")
//        private Integer materialNum;
    }



    // JsonIgnore
    @Schema(title = "分配方式")
    @JsonIgnore
    private String allotMode;

    @Schema(title = "问题集ID")
    @JsonIgnore
    private String questionSectionId;

    @Schema(title = "题数")
    @JsonIgnore
    private Integer questionCount;

    @Schema(title = "题型结构")
    @JsonIgnore
    private String questionSectionStructure;

    @Schema(title = "案例标示")
    @JsonIgnore
    private String caseIdentifier;

    @Schema(title = "版本号")
    @JsonIgnore
    private String ver;

    @Schema(title = "创建者账号Id")
    @JsonIgnore
    private String accountId;

    @Schema(title = "创建者姓名")
    @JsonIgnore
    private String accountName;

}
