package org.dows.hep.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentSchemeScore", title = "实验方案得分")
@TableName("experiment_scheme_score")
public class ExperimentSchemeScoreEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "方案设计评分ID")
    private String experimentSchemeScoreId;

    @Schema(title = "'案例方案设计ID'")
    private String caseSchemeId;

    @Schema(title = "实验方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "评审账号ID")
    private String reviewAccountId;

    @Schema(title = "评审得分")
    private Float reviewScore;

    @Schema(title = "评审时间")
    private Date reviewDt;

    @Schema(title = "评审状态[0-未提交|1-待审批|2-已审批]")
    private Integer reviewState;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
