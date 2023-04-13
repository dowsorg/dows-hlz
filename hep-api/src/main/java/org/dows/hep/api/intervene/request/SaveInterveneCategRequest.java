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
@Schema(name = "SaveInterveneCateg 对象", title = "类别信息")
public class SaveInterveneCategRequest{
    @Schema(title = "分布式id")
    private String eventCategId;

    @Schema(title = "名称")
    private String categName;

    @Schema(title = "分布式父id")
    private String categPid;

    @Schema(title = "类别key")
    private String section;


}
