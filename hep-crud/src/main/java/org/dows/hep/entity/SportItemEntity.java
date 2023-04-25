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
 * 运动项目(SportItem)实体类
 *
 * @author lait
 * @since 2023-04-24 10:23:50
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "SportItem", title = "运动项目")
@TableName("sport_item")
public class SportItemEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "运动项目id")
    private String sportItemId;

    @Schema(title = "运动项目名称")
    private String sportItemName;

    @Schema(title = "图片")
    private String pic;

    @Schema(title = "分类id")
    private String interveneCategId;

    @Schema(title = "分类名称")
    private String categName;

    @Schema(title = "分布id路径")
    private String categIdPath;

    @Schema(title = "分类名称路径")
    private String categNamePath;

    @Schema(title = "运动强度(MET)")
    private String strengthMet;

    @Schema(title = "运动强度类别")
    private String strengthType;

    @Schema(title = "状态 0-启用 1-停用")
    private Integer state;

    @Schema(title = "说明")
    private String descr;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

