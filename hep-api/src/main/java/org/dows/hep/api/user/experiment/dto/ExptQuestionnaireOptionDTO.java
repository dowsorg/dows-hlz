package org.dows.hep.api.user.experiment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @version 1.0
 * @description 实验知识答题选项DTO
 * @date 2023/6/20 10:40
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExptQuestionnaireOptionDTO 对象", title = "知识答题选项")
public class ExptQuestionnaireOptionDTO {
    @Schema(title = "id")
    private String id;

    @Schema(title = "选项标题")
    private String title;

    @Schema(title = "选项值")
    private String value;

    @Schema(title = "是否选中")
    private boolean choose = false;
}
