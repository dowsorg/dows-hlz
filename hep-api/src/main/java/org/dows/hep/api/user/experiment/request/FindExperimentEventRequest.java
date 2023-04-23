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
@Schema(name = "FindExperimentEvent 对象", title = "查询条件")
public class FindExperimentEventRequest{
    @Schema(title = "分页大小")
    private Integer pageSize;

    @Schema(title = "页码")
    private Integer pageNo;

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "实验小组id")
    private String experimentGroupId;

    @Schema(title = "实验人物ID")
    private String experimentPersonId;

    @Schema(title = "期数")
    private Integer periods;


}
