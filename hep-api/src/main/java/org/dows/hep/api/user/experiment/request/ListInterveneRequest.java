package org.dows.hep.api.user.experiment.request;

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
@Schema(name = "ListIntervene 对象", title = "查询条件")
public class ListInterveneRequest{
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "操作人id")
    private String operateAccountId;

    @Schema(title = "实验人物ID")
    private String experimentPsersonId;

    @Schema(title = "期数")
    private Integer periods;

    @Schema(title = "操作[干预]类型1-饮食 2-运动 3-心理 4-治疗 ")
    private String operateType;


}
