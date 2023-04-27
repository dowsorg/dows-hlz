package org.dows.hep.api.tenant.excel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dows.framework.doc.api.annotation.ExcelProperty;

/**
 * @author jx
 * @date 2023/4/24 11:54
 */
@Data
@Schema(name = "BatchMemberInsertDTO", title = "批量学生插入")
public class BatchMemberInsertRequest {
    @ExcelProperty(headName = "用户账号(code)", index = 0)
    private String accountName;

    @ExcelProperty(headName = "用户姓名", index = 0)
    private String userName;

    private String tips;
}
