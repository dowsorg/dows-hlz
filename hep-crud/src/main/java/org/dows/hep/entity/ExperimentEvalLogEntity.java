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
 * @date : 2023/9/5 16:37
 */

@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentEvalLog", title = "实验指标计算历史记录")
@TableName("experiment_eval_log")
public class ExperimentEvalLogEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "指标计算记录id")
    private String experimentEvalLogId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;
    @Schema(title = "计算批次")
    private Integer evalNo;

    @Schema(title = "功能点类型 1-期数翻转 2-治疗干预 3-健康指导 4-监测随访")
    private Integer funcType;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "计算天数")
    private Integer evalDay;

    @Schema(title = "计算时间")
    private Date evalTime;

    @Schema(title = "上次计算天数")
    private Integer lastEvalDay;

    @Schema(title = "当前健康指数")
    private String healthIndex;

    @Schema(title = "上次健康指数")
    private String lastHealthIndex;

    @Schema(title = "本次资金")
    private String money;

    @Schema(title = "上次资金")
    private String lastMoney;



    @Schema(title = "医疗占比")
    private String moneyScore;

    @Schema(title = "危险因素")
    private String risks;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;



}
