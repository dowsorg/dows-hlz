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
@Schema(name = "GroupRanking 对象", title = "小组排行")
public class GroupRankingRequest{
    @Schema(title = "实验实列ID")
    private String experimentInstanceId;

    @Schema(title = "期数")
    private String periods;


}
