package org.dows.hep.biz.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/4/21 16:12
 */

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class CategVO {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;
    @Schema(title = "根类别")
    private String family;

    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "父类别id")
    private String categPid;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "父类别路径")
    private String categIdPath;

    @Schema(title = "父名称路径")
    private String categNamePath;

    @Schema(title = "标记，0-普通 1-膳食主要分类")
    private Integer mark;

    @Schema(title = "扩展属性，饮食推荐量")
    private FoodCategExtendVO extend;

    @Schema(title = "排序号")
    private Integer seq;



    @Schema(title = "子类别json")
    private List<CategVO> childs;


}
