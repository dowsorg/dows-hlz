package org.dows.hep.api.tenant.casus.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * @author fhb
 * @description
 * @date 2023/5/18 17:00
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "CaseQuestionSearchRequest 对象", title = "问题搜索")
public class CaseQuestionSearchRequest {
    @Schema(title = "案例ID")
    private String caseInstanceId;

    @Schema(title = "知识体系")
    private String l1CategId;

    @Schema(title = "知识类别")
    private String l2CategId;

    @Schema(title = "题型")
    private String questionType;

    @Schema(title = "题目关键字")
    private String keyword;
}
