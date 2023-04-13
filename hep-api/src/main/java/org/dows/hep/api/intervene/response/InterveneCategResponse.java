package org.dows.hep.api.intervene.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;
import java.util.Date;
import java.math.BigDecimal;

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
    @Schema(title = "类别key")
    private String section;

    @Schema(title = "分布式id")
    private String eventCategId;

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
