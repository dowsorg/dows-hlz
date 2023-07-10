package org.dows.hep.api.report.pdf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告基本信息
 * @date 2023/7/7 10:52
 **/
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptBaseReportVO 对象", title = "实验报告基本信息")
public class ExptBaseInfo {
    @Schema(title = "标题")
    private String title;

    @Schema(title = "logo图片")
    private String logoImg;

    @Schema(title = "封面图片")
    private String coverImg;

    @Schema(title = "版权")
    private String copyRight;
}
