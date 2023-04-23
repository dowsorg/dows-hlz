package org.dows.hep.entity;

import java.util.Date;

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

/**
 * 实验扮演者(ExperimentActor)实体类
 *
 * @author lait
 * @since 2023-04-23 09:47:01
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentActor", title = "实验扮演者")
@TableName("experiment_actor")
public class ExperimentActorEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "实验扮演者ID")
    private String experimentActorId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "扮演关联ID[分析角色|机构角色]")
    private String actorId;

    @Schema(title = "分配角色[分析角色名|机构角色名]")
    private String actorName;

    @Schema(title = "扮演类型[0:问题，1:机构]")
    private String actorType;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "组员账号ID")
    private String accountId;

    @Schema(title = "组员账号名")
    private String accountName;

    @Schema(title = "参与者类型[0:教师，1:学生，2:组长]")
    private Integer participatorType;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

