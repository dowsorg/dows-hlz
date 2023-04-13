package org.dows.hep.api.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "SurveyInfo 对象", title = "问卷信息")
public class SurveyInfoResponse{
    @Schema(title = "分布式id")
    private String surveyId;

    @Schema(title = "问卷名称")
    private String surveyName;

    @Schema(title = "分类id")
    private String surveyCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "问卷说明")
    private String descr;

    @Schema(title = "维度列表json")
    private String dimensions;

    @Schema(title = "问题列表json")
    private String questions;

    @Schema(title = "维度公式json")
    private String expressions;

    @Schema(title = "报告列表json")
    private String reports;


}
