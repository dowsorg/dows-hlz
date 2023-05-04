package org.dows.hep.entity;

import java.math.BigDecimal;
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
 * 排行榜Item(RankListItem)实体类
 *
 * @author lait
 * @since 2023-04-28 10:29:02
 */
@SuppressWarnings("serial")
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "RankListItem", title = "排行榜Item")
@TableName("rank_list_item")
public class RankListItemEntity implements CrudEntity {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "主键ID")
    private Long id;

    @Schema(title = "排行榜Item Id")
    private String rankingListItemId;

    @Schema(title = "排行榜Id")
    private String rankingListId;

    @Schema(title = "名次")
    private Integer ranking;

    @Schema(title = "标题")
    private String title;

    @Schema(title = "子标题")
    private String subTitle;

    @Schema(title = "分值")
    private BigDecimal score;

    @Schema(title = "单位")
    private String unit;

    @Schema(title = "图标")
    private String icon;

    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "逻辑删除")
    private Boolean deleted;

    @TableField(fill = FieldFill.INSERT)
    @Schema(title = "时间戳")
    private Date dt;

}

