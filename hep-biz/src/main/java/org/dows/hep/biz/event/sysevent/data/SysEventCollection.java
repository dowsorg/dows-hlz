package org.dows.hep.biz.event.sysevent.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/8/22 11:58
 */

@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class SysEventCollection  {

    @Schema(title = "实验实例id")
    private String experimentInstanceId;

    @Schema(title = "初始化标记")
    private boolean initFlag;

    @Schema(title = "事件列表")
    private List<SysEventRow> eventRows;

}
