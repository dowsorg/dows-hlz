package org.dows.hep.api.user.experiment.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

/**
 * @author fhb
 * @description
 * @date 2023/5/30 15:54
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "ExperimentSchemeResponse 对象", title = "实验方案设计")
public class ExperimentSchemeItemResponse {
    @Schema(title = "方案设计ID")
    private String experimentSchemeId;

    @Schema(title = "item Id")
    private String experimentSchemeItemId;

    @Schema(title = "问题标题")
    private String questionTitle;

    @Schema(title = "问题描述")
    private String questionDescr;

    @Schema(title = "作答人账号ID")
    private String accountId;

    @Schema(title = "答题结果")
    private String questionResult;

    @Schema(title = "是否可以编辑")
    private Boolean canEdit;

    @Schema(title = "子")
    private List<ExperimentSchemeItemResponse> children = new ArrayList<>();

    @Schema(title = "item pid")
    @JsonIgnore
    private String experimentSchemeItemPid;

    public void addChild(ExperimentSchemeItemResponse child) {
        this.children.add(child);
    }
}
