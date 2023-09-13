package org.dows.hep.rest.user.edw;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.edw.domain.HepOperateCost;
import org.dows.hep.api.edw.request.HepOperateCostGetRequest;
import org.dows.hep.api.edw.request.HepOperateCostSetRequest;
import org.dows.hep.api.edw.response.HepOperateCostResponse;
import org.dows.hep.biz.edw.HepOperateCostGetBiz;
import org.dows.hep.biz.edw.HepOperateCostSetBiz;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fhb
 * @version 1.0
 * @description 实验操作费用记录
 * @date 2023/9/13 13:35
 * @folder user-hep/数仓
 **/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验操作费用记录", description = "实验操作费用记录")
public class HepOperateCostRest {

    private final HepOperateCostGetBiz hepOperateCostGetBiz;
    private final HepOperateCostSetBiz hepOperateCostSetBiz;

    /**
     * @param request - 新增或更新请求参数
     * @return HepOperateCost
     * @author fhb
     * @description 新增或更新操作费用记录
     * @date 2023/9/13 15:12
     *
     * 新增或更新操作费用记录
     */
    @Operation(summary = "新增或更新操作费用记录")
    @PostMapping("v1/hepOperateCost/setOperateCost")
    public HepOperateCostResponse setOperateCost(@RequestBody HepOperateCostSetRequest request) {
        HepOperateCost hepOperateCost = hepOperateCostSetBiz.setOperateEntity(request);

        if (BeanUtil.isEmpty(hepOperateCost)) {
            return new HepOperateCostResponse();
        }
        return BeanUtil.copyProperties(hepOperateCost, HepOperateCostResponse.class);
    }

    /**
     * @param request - 请求参数
     * @return HepOperateCost
     * @author fhb
     * @description 查询操作费用记录
     * @date 2023/9/13 15:12
     *
     * 查询操作费用记录
     */
    @Operation(summary = "查询操作费用记录")
    @PostMapping("v1/hepOperateCost/getOperateCost")
    public HepOperateCostResponse getOperateCost(@RequestBody HepOperateCostGetRequest request) {
        HepOperateCost hepOperateCost = hepOperateCostGetBiz.getOperateEntity(request);

        if (BeanUtil.isEmpty(hepOperateCost)) {
            return new HepOperateCostResponse();
        }
        return BeanUtil.copyProperties(hepOperateCost, HepOperateCostResponse.class);
    }
}
