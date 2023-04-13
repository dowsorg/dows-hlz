package org.dows.hep.api.experiment.user.request;

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
@Schema(name = "SaveOperateIntevene 对象", title = "保存干预记录")
public class SaveOperateInteveneRequest{
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "操作人名")
    private String operateAccountName;

    @Schema(title = "案例人物")
    private String caseAccountId;

    @Schema(title = "案例人名")
    private String caseAccountName;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作[干预]类型 1-饮食 2-运动 3-心理 4-治疗 ")
    private String operateType;

    @Schema(title = "状态完整快照json")
    private String operateContextJson;


}
