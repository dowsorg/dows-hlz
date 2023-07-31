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
 * 实验实列(ExperimentInstance)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:29
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentInstance", title = "实验实列")
@TableName("experiment_instance")
public class ExperimentInstanceEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例名称[社区名]")
    private String caseName;
    @Schema(title = "案例图示[社区图片]")
    private String casePic;

    @Schema(title = "实验名称")
    private String experimentName;

    @Schema(title = "实验说明")
    private String experimentDescr;

    @Schema(title = "实验参与者ID集合，以逗号分隔")
    private String experimentParticipatorIds;

    @Schema(title = "分配人ID")
    private String accountId;

    @Schema(title = "分配人账号")
    private String appointor;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "开始时间")
    private Date startTime;

    @Schema(title = "结束时间")
    private Date endTime;

    @Schema(title = "实验状态[默认未开始状态0~6步][0-未开始 1-已开始 2-暂停]")
    private Integer state;

    @Schema(title = "分配人姓名")
    private String appointorName;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

