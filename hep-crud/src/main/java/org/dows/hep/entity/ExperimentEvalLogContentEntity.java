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
import org.dows.framework.crud.api.CrudEntity;

import java.util.Date;

/**
 * @author : wuzl
 * @date : 2023/9/5 17:17
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentEvalLogContent", title = "指标计算详情")
@TableName("experiment_eval_log_content")
public class ExperimentEvalLogContentEntity  implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "指标计算详情id")
    private String experimentEvalLogContentId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标计算id")
    private String experimentEvalLogId;

    @Schema(title = "计算批次")
    private Integer evalNo;

    @Schema(title = "指标计算内容")
    private String indicatorContent;

    @Schema(title = "健康指数计算内容")
    private String healthIndexContent;

    @Schema(title = "判断操作内容")
    private String judgeItemsContent;



    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;



}
