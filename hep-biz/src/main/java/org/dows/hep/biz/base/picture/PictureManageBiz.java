package org.dows.hep.biz.base.picture;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.materials.request.MaterialsRequest;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/4/24 15:42
 */
@Service
@RequiredArgsConstructor
public class PictureManageBiz {
    /**
     * @param
     * @return
     * @说明: 新增 图示
     * @关联表:
     * @工时: 2H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023/4/24 15:49
     */
    @DSTransactional
    public String savePicture(MaterialsRequest materials) {
        return "";
    }
}
