package org.dows.hep.api.tenant.experiment.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author fhb
 * @version 1.0
 * @description 个人查询报告分页响应
 * @date 2023/7/31 15:40
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ExptAccountReportResponse 对象", title = "个人查询报告分页响应")
public class ExptAccountReportResponse {
    @Schema(title = "实验ID")
    private String exptInstanceId;

    @Schema(title = "实验名称")
    private String exptName;

    @Schema(title = "实验模式")
    private String exptMode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "实验开始时间")
    private Date exptStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(title = "实验结束时间")
    private Date exptEndTime;
}
