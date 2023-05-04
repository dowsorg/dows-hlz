package org.dows.hep.api.base.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "InterveneCateg 对象", title = "类别信息列表")
public class InterveneCategResponse{
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @Schema(title = "数据库id")
    private Long id;

    @Schema(title = "类别key")
    private String family;

    @Schema(title = "分布式id")
    private String interveneCategId;

    @Schema(title = "分布式父id")
    private String categPid;

    @Schema(title = "名称")
    private String categName;

    @Schema(title = "分布式id路径")
    private String categIdPath;

    @Schema(title = "名称路径")
    private String categNamePath;

    @Schema(title = "排序号")
    private Integer seq;

    @Schema(title = "子类别json")
    private String childs;


}
