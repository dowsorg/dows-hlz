package org.dows.hep.rest.base.intervene;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.response.InterveneCategResponse;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.biz.base.intervene.InterveneCategBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:52
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "干预类别管理", description = "干预类别管理")
public class InterveneCategRest {
    private final InterveneCategBiz interveneCategBiz;

    /**
    * 获取类别
    * @param
    * @return
    */
    @Operation(summary = "获取类别")
    @PostMapping("v1/baseIntervene/interveneCateg/listInterveneCateg")
    public List<InterveneCategResponse> listInterveneCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return interveneCategBiz.listInterveneCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @Operation(summary = "保存类别")
    @PostMapping("v1/baseIntervene/interveneCateg/saveInterveneCateg")
    public Boolean saveInterveneCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) {
        return interveneCategBiz.saveInterveneCateg(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @Operation(summary = "删除类别")
    @DeleteMapping("v1/baseIntervene/interveneCateg/delInterveneCateg")
    public Boolean delInterveneCateg(@Validated DelInterveneCategRequest delInterveneCateg ) {
        return interveneCategBiz.delInterveneCateg(delInterveneCateg);
    }


}