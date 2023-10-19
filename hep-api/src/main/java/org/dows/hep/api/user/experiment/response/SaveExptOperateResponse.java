package org.dows.hep.api.user.experiment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author : wuzl
 * @date : 2023/10/19 11:00
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SaveExptOperate 对象", title = "实验操作结果")

public class SaveExptOperateResponse {
    @Schema(title = "是否操作成功")
    private Boolean success;
    @Schema(title = "机构操作记录id")
    private String operateOrgFuncId;
}
