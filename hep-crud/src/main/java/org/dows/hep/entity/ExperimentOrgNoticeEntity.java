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
 * 实验机构通知(ExperimentOrgNotice)实体类
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
@Schema(name = "ExperimentOrgNotice", title = "实验机构通知")
@TableName("experiment_org_notice")
public class ExperimentOrgNoticeEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验机构id")
    private String experimentOrgId;

    @Schema(title = "实验人物id")
    private String experimentPersonId;

    @Schema(title = "人物名称")
    private String personName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "游戏内天数")
    private Integer gameDay;

    @Schema(title = "通知时间")
    private Date noticeTime;

    @Schema(title = "通知类型 1-人物转移 2-检测随访 3-突发事件")
    private Integer noticeSrcType;

    @Schema(title = "通知来源id，转移，随访操作id，事件id")
    private String noticeSrcId;

    @Schema(title = "uid人物id")
    private String accountId;

    @Schema(title = "人物头像")
    private String avatar;
    @Schema(title = "通知标题")
    private String title;

    @Schema(title = "通知内容")
    private String content;

    @Schema(title = "操作提示")
    private String tips;

    @Schema(title = "事件处理措施列表")
    private String eventActions;

    @Schema(title = "通知状态，0-未读 1-已读")
    private Integer readState;

    @Schema(title = "处理状态，0-无需处理 1-待处理 2-已处理")
    private Integer actionState;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

