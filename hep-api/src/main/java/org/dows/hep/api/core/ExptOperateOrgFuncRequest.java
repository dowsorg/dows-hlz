package org.dows.hep.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 学生端机构操作记录请求基类
 *
 * @author : wuzl
 * @date : 2023/6/5 13:36
 */
@Data
@NoArgsConstructor
@Schema(name = "ExptOperateOrgFunc 对象", title = "学生端机构操作记录")
public class ExptOperateOrgFuncRequest extends ExptOrgFuncRequest {
    @Schema(title = "机构操作记录id,非空时仅以此查询记录")
    private String operateOrgFuncId;
}
