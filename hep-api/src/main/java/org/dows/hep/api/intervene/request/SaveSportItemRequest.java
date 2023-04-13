package org.dows.hep.api.intervene.request;

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
@Schema(name = "SaveSportItem 对象", title = "运动项目信息")
public class SaveSportItemRequest{
    @Schema(title = "运动项目id，新增时为空")
    private String sportItemId;

    @Schema(title = "运动项目名称")
    private String sportItemName;

    @Schema(title = "图片")
    private String pic;

    @Schema(title = "当前分类id")
    private String interveneCategId;

    @Schema(title = "运动强度(MET)")
    private String strengthMet;

    @Schema(title = "运动强度类别")
    private String strengthType;

    @Schema(title = "关联指标json对象")
    private String indicators;


}
