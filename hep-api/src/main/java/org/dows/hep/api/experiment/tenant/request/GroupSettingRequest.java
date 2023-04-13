package org.dows.hep.api.experiment.tenant.request;

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
@Schema(name = "GroupSetting 对象", title = "小组设置")
public class GroupSettingRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "小组序号")
    private String groupNo;

    @Schema(title = "小组别名[第1组，第2组...]")
    private String groupAlias;

    @Schema(title = "成员数量")
    private Integer memberCount;

    @Schema(title = "参与者Json对象")
    private String experimentParticipator;


}
