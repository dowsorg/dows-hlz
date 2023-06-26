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

import java.math.BigDecimal;
import java.util.Date;

/**
 * 治疗项目(TreatItem)实体类
 *
 * @author lait
 * @since 2023-04-28 10:29:08
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "TreatItem", title = "治疗项目")
@TableName("treat_item")
public class TreatItemEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "分布式id")
    private String treatItemId;

    @Schema(title = "应用ID")
    private String appId;

    @Schema(title = "治疗名称")
    private String treatItemName;

    @Schema(title = "功能点类别")
    private String indicatorCategoryId;

    @Schema(title = "功能点id")
    private String indicatorFuncId;

    @Schema(title = "分类id")
    private String interveneCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "一级分类id")
    private String categIdLv1;

    @Schema(title = "一级分类名称")
    private String categNameLv1;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "费用")
    private BigDecimal fee;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

