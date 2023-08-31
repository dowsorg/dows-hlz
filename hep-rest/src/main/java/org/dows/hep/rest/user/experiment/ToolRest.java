package org.dows.hep.rest.user.experiment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.biz.user.experiment.ToolBiz;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @folder user-hep/tools
 * @author : wuzl
 * @date : 2023/8/31 12:15
 */
@RequiredArgsConstructor
@RestController
@Tag(name = "开发用工具", description = "开发用工具")
public class ToolRest {

    private final ToolBiz toolBiz;
    @Operation(summary = "获取webSocket连接状态")
    @GetMapping("v1/tool/getWebSocketState")
    public String getWebSocketState(@RequestParam String exptId){
        return toolBiz.getWebSocketState(exptId);
    }
}
