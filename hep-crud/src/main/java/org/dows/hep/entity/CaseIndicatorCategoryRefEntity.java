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
 * 指标分类与指标关联关系(IndicatorCategoryRef)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:45
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "CaseIndicatorCategoryRefEntity", title = "指标分类与指标关联关系")
@TableName("case_indicator_category_ref")
public class CaseIndicatorCategoryRefEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "案例分布式ID")
    private String caseIndicatorCategoryRefId;

    @Schema(title = "分布式ID")
    private String indicatorCategoryRefId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "指标类别分布式ID")
    private String indicatorCategoryId;

    @Schema(title = "分布式ID")
    private String indicatorInstanceId;

    @Schema(title = "展示顺序")
    private Integer seq;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

