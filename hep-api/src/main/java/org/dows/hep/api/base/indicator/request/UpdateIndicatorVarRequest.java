package org.dows.hep.api.base.indicator.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
* @description 
*
* @author 
* @date 
*/
@Data
@NoArgsConstructor
@Schema(name = "UpdateIndicatorVar 对象", title = "更新指标变量")
public class UpdateIndicatorVarRequest{
    @Schema(title = "指标ID")
    private String indicatorInstanceId;

    @Schema(title = "数据库名")
    private String dbName;

    @Schema(title = "表名")
    private String tbName;

    @Schema(title = "变量名")
    private String varName;

    @Schema(title = "变量code")
    private String varCode;

    @Schema(title = "期数，如果多期用[,]分割")
    private String periods;

    @Schema(title = "描述")
    private String descr;


}
