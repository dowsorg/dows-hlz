package org.dows.hep.api.core;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 学生端机构操作请求基类
 * @author : wuzl
 * @date : 2023/5/29 16:19
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "ExptOrgFunc 对象", title = "学生端机构操作")
public class ExptOrgFuncRequest extends BaseExptRequest {


    @Schema(title = "指标功能点ID")
    private String indicatorFuncId;




}
