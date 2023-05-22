package org.dows.hep.api.base.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.dows.hep.api.base.question.enums.QuestionTypeEnum;
import org.dows.hep.api.base.question.request.QuestionRequest;

/**
 * @author fhb
 * @description
 * @date 2023/5/22 9:20
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionRequestDTO 对象", title = "问题Request")
public class QuestionRequestDTO {
    private QuestionRequest questionRequest;

    // 问题ID
    private String questionInstanceId;

    // 题目答题类型[RADIO_SELECT:单选题|MULTIPLE_SELECT:多选题|JUDGMENT:判断题|SUBJECTIVE:主观题|MATERIAL:材料题]
    private QuestionTypeEnum questionType;

    // 应用ID
    private String appId;

    // 问题PID
    private String questionInstancePid;

    // 来源
    private String source;

    // biz code: PUBLIC_VIEWING：被所有人查看 | PRIVATE_VIEWING：只能被自己查看
    private String bizCode;

    // 创建者账号Id
    private String accountId;

    // 创建者姓名
    private String accountName;
}
