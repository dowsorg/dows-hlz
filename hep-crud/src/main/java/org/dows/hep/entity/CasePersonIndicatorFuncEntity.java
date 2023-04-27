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
 * 人物功能点(CasePersonIndicatorFunc)实体类
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
@Schema(name = "CasePersonIndicatorFunc", title = "人物功能点")
@TableName("case_person_indicator_func")
public class CasePersonIndicatorFuncEntity implements CrudEntity {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库ID")
    private Long id;

    @Schema(title = "案例人物功能点ID")
    private String casePersonIndicatorFuncId;

    @Schema(title = "指标分类ID")
    private String indicatorCategoryId;

    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;

    @Schema(title = "案例人物ID")
    private String casePersonId;

    @Schema(title = "背景图片")
    private String background;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;
}
