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
 * 实验机构通知(ExperimentOrgNotice)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:48
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ExperimentOrgNotice", title = "实验机构通知")
@TableName("experiment_org_notice")
public class ExperimentOrgNoticeEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "案例机构ID")
    private String caseOrgId;

    @Schema(title = "案例账号ID")
    private String caseAccountId;

    @Schema(title = "账号名称")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "通知类型 1-人物转移 2-检测随访 3-突发事件")
    private Boolean noticeType;

    @Schema(title = "通知来源id，转移，随访操作id，事件id")
    private String noticeSrcId;

    @Schema(title = "通知内容")
    private String content;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

