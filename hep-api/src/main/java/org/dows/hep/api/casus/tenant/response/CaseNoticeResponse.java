package org.dows.hep.api.casus.tenant.response;

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
@Schema(name = "CaseNotice 对象", title = "案例公告集合")
public class CaseNoticeResponse{
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "案例公告ID")
    private String caseNoticeId;

    @Schema(title = "公告名称")
    private String noticeName;

    @Schema(title = "公告内容")
    private String noticeContent;

    @Schema(title = "期数")
    private String periods;

    @Schema(title = "期数排序")
    private Integer periodSequence;


}
