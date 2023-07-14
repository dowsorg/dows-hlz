package org.dows.hep.api.report.pdf;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @version 1.0
 * @description TODO
 * @date 2023/7/10 10:52
 **/
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptGroupReportVO 对象", title = "实验小组报告")
public class ExptGroupReportVO {
    @Schema(title = "组ID")
    private String exptGroupId;

    @Schema(title = "组号")
    private Integer exptGroupNo;

    @Schema(title = "文件路径")
    private List<ReportFile> paths;

    @Data
    @Builder
    public static class ReportFile {
        private String name;
        private String path;
    }

    public static ExptGroupReportVO emptyVO() {
        return ExptGroupReportVO.builder()
                .paths(new ArrayList<>())
                .build();
    }
}
