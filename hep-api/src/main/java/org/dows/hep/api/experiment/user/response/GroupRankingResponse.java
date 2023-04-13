package org.dows.hep.api.experiment.user.response;

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
@Schema(name = "GroupRanking 对象", title = "小组排行榜")
public class GroupRankingResponse{
    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组名称")
    private String groupName;

    @Schema(title = "总分")
    private String totalSocre;

    @Schema(title = "健康指数占比")
    private String healthIndexPercent;

    @Schema(title = "知识考点占比")
    private String knowledgeScorePercent;

    @Schema(title = "医疗占比")
    private String medicalProportionPercent;


}
