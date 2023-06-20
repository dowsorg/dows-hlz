package org.dows.hep.api.user.experiment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 实验案例信息
 * @date 2023/6/20 11:54
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptCaseNoticeDTO 对象", title = "实验案例信息")
public class ExptCaseNoticeDTO {
    @Schema(title = "期数")
    private Integer period;

    @Schema(title = "公告名称")
    private String noticeName;

    @Schema(title = "公告内容")
    private String noticeContent;
}
