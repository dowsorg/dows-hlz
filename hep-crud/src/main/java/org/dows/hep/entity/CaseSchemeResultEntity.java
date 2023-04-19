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
 * 案例方案结果(CaseSchemeResult)实体类
 *
 * @author lait
 * @since 2023-04-18 13:54:32
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseSchemeResult", title = "案例方案结果")
@TableName("case_scheme_result")
public class CaseSchemeResultEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例方案结果ID")
    private String caseSchemeResultId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "方案ID")
    private String caseSchemeId;

    @Schema(title = "问题集ID")
    private String questionSectionId;

    @Schema(title = "答题记录ID")
    private String questionSectionResultId;

    @Schema(title = "答题者账号ID")
    private String accountId;

    @Schema(title = "答题者Name")
    private String accountName;

    @Schema(title = "问题ids[1,2]")
    private String questionInstanceIds;

    @Schema(title = "状态[0-未开始|1-进行中|2-已完成]")
    private Boolean status;

    @Schema(title = "持续时间[min]")
    private Integer duration;

    @Schema(title = "案例标示")
    private String caseIdentifier;

    @Schema(title = "版本号")
    private String ver;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

