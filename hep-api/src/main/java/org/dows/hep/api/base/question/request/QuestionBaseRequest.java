package org.dows.hep.api.base.question.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author fhb
 * @description 所有问题域的 Request 都继承该 BaseRequest，如果系统有 Base，则由 QuestionBaseRequest 继承即可。
 * @date 2023/4/19 15:23
 */
@Data
@NoArgsConstructor
@Schema(name = "QuestionBaseRequest 对象", title = "问题 Base Request")
public class QuestionBaseRequest {

    @Schema(title = "应用ID")
    private String appId;

}
