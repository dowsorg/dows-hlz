package org.dows.hep.api.base.question.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author fhb
 * @description
 * @date 2023/6/1 17:47
 */
@Data
@ToString
@Builder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "QuestionRequestDTO 对象", title = "问题Request")
public class QuestionResultRecordDTO {
    private Map<String, String> questionResultMap;
}
