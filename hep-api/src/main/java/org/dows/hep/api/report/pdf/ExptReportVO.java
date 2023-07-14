package org.dows.hep.api.report.pdf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description 实验报告响应
 * @date 2023/7/10 10:23
 **/

@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptReportVO 对象", title = "实验报告")
public class ExptReportVO {

    @Schema(title = "小组报告集合")
    private List<ExptGroupReportVO> groupReportList;

    @Schema(title = "压缩包路径")
    private String zipPath;

    @Schema(title = "压缩名")
    private String zipName;

    public static ExptReportVO emptyVO() {
        return ExptReportVO.builder()
                .groupReportList(new ArrayList<>())
                .build();
    }
}
