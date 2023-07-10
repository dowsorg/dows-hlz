package org.dows.hep.api.user.experiment.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.dows.hep.api.core.BaseExptRequest;
import org.dows.hep.api.user.experiment.vo.ExptOrgNoticeActionVO;

import java.util.List;

/**
 * @author : wuzl
 * @date : 2023/7/8 21:52
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SaveNoticeAction 对象", title = "突发事件处理")

public class SaveNoticeActionRequest extends BaseExptRequest {
    @Schema(title = "机构通知id")
    private String experimentOrgNoticeId;

    @Schema(title = "突发事件处理措施列表")
    private List<ExptOrgNoticeActionVO> actions;



}
