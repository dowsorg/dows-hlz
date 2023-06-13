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
 * 实验组员（参与者）(ExperimentParticipator)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:31
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentParticipator", title = "实验组员（参与者）")
@TableName("experiment_participator")
public class ExperimentParticipatorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验参与者ID")
    private String experimentParticipatorId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "实验名称")
    private String experimentName;



    @Schema(title = "实验机构ID")
    private String experimentOrgIds;

    @Schema(title = "实验机构名称")
    private String experimentOrgNames;

    @Schema(title = "实验方案设计ItemId")
    private String experimentSchemeItemIds;

    @Schema(title = "组员账号ID")
    private String accountId;

    @Schema(title = "组员账号名")
    private String accountName;

    @Schema(title = "组名")
    private String groupAlias;

    @Schema(title = "组序号")
    private String groupNo;

    @Schema(title = "实验模式[0:标准模式，1:沙盘模式，2:方案设计模式]")
    private Integer model;

    @Schema(title = "参与者序号")
    private Integer participatorNo;

    @Schema(title = "参与者类型[0:教师，1:组长，2：学生]")
    private Integer participatorType;

    @Schema(title = "参与者状态[0: 未准备 1:已准备 2:选择阶段中 3:已选择阶段]")
    private Integer participatorState;

    @Schema(title = "实验者状态[默认未开始状态0~6步]")
    private Integer state;


    @Schema(title = "参与者加入时间")
    private Date joinTime;

    @Schema(title = "实验开始时间")
    private Date experimentStartTime;

    @Schema(title = "实验结束时间")
    private Date experimentEndTime;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;


    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;
}

