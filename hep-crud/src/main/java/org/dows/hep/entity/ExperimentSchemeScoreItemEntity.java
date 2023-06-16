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
@Schema(name = "ExperimentSchemeScoreItem", title = "实验方案得分Item")
@TableName("experiment_scheme_score_item")
public class ExperimentSchemeScoreItemEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "方案设计评分ItemId")
    private String experimentSchemeScoreItemId;

    @Schema(title = "方案设计评分ID")
    private String experimentSchemeScoreId;

    @Schema(title = "维度名")
    private String dimensionName;

    @Schema(title = "维度说明")
    private String dimensionContent;

    @Schema(title = "最小得分限制")
    private Float minScore;

    @Schema(title = "最大得分限制")
    private Float maxScore;

    @Schema(title = "最终得分")
    private Float score;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
