package org.dows.hep.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.intervene.vo.FoodCategExtendVO;

import java.util.List;

/**
 * 学生端类别
 * @author : wuzl
 * @date : 2023/4/21 16:12
 */

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "Categ4ExptVO 对象", title = "实验干预类别")
public class Categ4ExptVO {


    @Schema(title = "根类别")
    private String family;

    @Schema(title = "类别id")
    private String categId;

    @Schema(title = "父类别id")
    private String categPid;

    @Schema(title = "类别名称")
    private String categName;

    @Schema(title = "排序号")
    private Integer seq;

    @Schema(title = "扩展")
    private String spec;

    @Schema(title = "扩展属性，上下限")
    private FoodCategExtendVO extend;


    @Schema(title = "子类别json")
    private List<Categ4ExptVO> childs;




}
