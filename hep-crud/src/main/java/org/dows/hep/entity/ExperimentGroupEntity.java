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
import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * 实验小组(ExperimentGroup)实体类
 *
 * @author lait
 * @since 2023-04-28 10:25:27
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentGroup", title = "实验小组")
@TableName("experiment_group")
public class ExperimentGroupEntity implements CrudEntity,Comparable<ExperimentGroupEntity>{

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验小组ID")
    private String experimentGroupId;

    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "组名")
    private String groupName;

    @Schema(title = "小组别名")
    private String groupAlias;

    @Schema(title = "成员数量")
    private Integer memberCount;

    @Schema(title = "最小成员数量")
    private Integer minMemberCount;

    @Schema(title = "最大成员数量")
    private Integer maxMemberCount;

    @Schema(title = "实验状态[默认未开始状态0~6步]")
    private Boolean state;

    @Schema(title = "小组状态 [0-新建（待重新命名） 1-编队中 （分配成员角色） 2-编队完成 3-已锁定 4-已解散]")
    private Integer groupState;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

    @Override
    public int compareTo(@NotNull ExperimentGroupEntity o) {
        return Integer.parseInt(this.groupNo) - Integer.parseInt(o.groupNo);
    }
}

