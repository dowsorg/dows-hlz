package org.dows.hep.api.tenant.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告分页响应
 * @date 2023/7/31 11:06
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptReportPageRequest 对象", title = "实验报告分页响应")
public class ExptReportPageResponse {
    @Schema(title = "实验ID")
    private String exptInstanceId;

    @Schema(title = "实验名称")
    private String exptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "分配时间")
    private Date exptAllotTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "实验开始时间")
    private Date exptStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "实验结束时间")
    private Date exptEndTime;

    @Schema(title = "班级名称")
    private String clazzName;

    @Schema(title = "实验状态")
    private Integer exptState;

    @Schema(title = "实验状态名")
    private String exptStateName;

    @Schema(title = "分配者名称")
    private String allotUserName;

    @Schema(title = "实验模式")
    private String exptMode;

}
