package org.dows.hep.biz.event.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.dows.hep.entity.ExperimentEventEntity;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/6/17 23:51
 */
@Data
@Builder
@Accessors(chain = true)
public class PersonBasedEventCollection {
    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "事件列表")
    private List<PersonBasedEventGroup> eventGroups;

    @Data
    @Builder
    @Accessors(chain = true)
    public static class PersonBasedEventGroup {

        @Schema(title = "实验人物id")
        private String experimentPersonId;

        @Schema(title = "事件列表")
        private List<ExperimentEventEntity> eventItems;
    }
}
