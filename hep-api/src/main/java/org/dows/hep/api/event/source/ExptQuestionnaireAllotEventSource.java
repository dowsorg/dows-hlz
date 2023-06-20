package org.dows.hep.api.event.source;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author fhb
 * @version 1.0
 * @description 实验知识答题分配事件-事件源
 * @date 2023/6/20 0:09
 **/
@Data
@Builder
@AllArgsConstructor
@Schema(name = "ExptQuestionnaireAllotEventSource 对象", title = "实验知识答题分配事件-事件源")
public class ExptQuestionnaireAllotEventSource {
    // 实验ID
    private String experimentInstanceId;
    // 实验组ID
    private String experimentGroupId;
}
