package org.dows.hep.rest.user.edw;

import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.edw.HepOperateTypeEnum;
import org.dows.hep.api.edw.request.HepOperateGetRequest;
import org.dows.hep.api.edw.request.HepOperateSetRequest;
import org.dows.hep.api.edw.response.HepOperateResponse;
import org.dows.edw.repository.HepOperateGetRepository;
import org.dows.edw.repository.HepOperateSetRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * @author fhb
 * @version 1.0
 * @description 实验操作记录
 * @date 2023/9/13 13:34
 * @folder user-hep/数仓
 **/
@RequiredArgsConstructor
@RestController
@Tag(name = "实验操作记录", description = "实验操作记录")
public class HepOperateRest {

    private final HepOperateGetRepository hepOperateGetRepository;
    private final HepOperateSetRepository hepOperateSetRepository;

    /**
     * @param request - 请求参数
     * @return org.dows.hep.api.edw.response.HepOperateResponse
     * @author fhb
     * @description 获取操作记录
     * @date 2023/9/13 15:24
     *
     * 新增或更新操作记录
     */
    @Operation(summary = "新增或更新操作记录")
    @PostMapping("v1/hepOperate/getOperate")
    public HepOperateResponse getOperate(@RequestBody HepOperateGetRequest request) {
        HepOperateTypeEnum type = HepOperateTypeEnum.valueOf(request.getType());
        Class<?> clazz = type.getClazz();
        Object operateEntity = hepOperateGetRepository.getOperateEntity(request, clazz);

        if (Objects.isNull(operateEntity)) {
            return new HepOperateResponse();
        }
        return BeanUtil.copyProperties(operateEntity, HepOperateResponse.class);
    }

    /**
     * @param request - 请求参数
     * @return org.dows.hep.api.edw.response.HepOperateResponse
     * @author fhb
     * @description 保存操作记录
     * @date 2023/9/13 15:28
     *
     * 保存操作记录
     */
    @Operation(summary = "保存操作记录")
    @PostMapping("v1/hepOperate/setOperate")
    public HepOperateResponse setOperate(@RequestBody HepOperateSetRequest request) {
        HepOperateTypeEnum type = HepOperateTypeEnum.valueOf(request.getType());
        Class<?> clazz = type.getClazz();
        Object operateEntity = hepOperateSetRepository.setOperateEntity(request, clazz);

        if (Objects.isNull(operateEntity)) {
            return new HepOperateResponse();
        }
        return BeanUtil.copyProperties(operateEntity, HepOperateResponse.class);
    }

}
