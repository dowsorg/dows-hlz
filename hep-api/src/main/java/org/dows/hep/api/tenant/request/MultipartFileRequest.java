package org.dows.hep.api.tenant.request;

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
@Schema(name = "MultipartFile 对象", title = "上传文件")
public class MultipartFileRequest{
    @Schema(title = "notnull")
    private  1;


}
