package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.intervene.response.InterveneCategResponse;
import org.dows.hep.api.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.api.intervene.request.DelInterveneCategRequest;
import org.dows.hep.biz.intervene.InterveneCategBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月14日 上午10:19:59
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "干预类别管理")
public class InterveneCategRest {
    private final InterveneCategBiz interveneCategBiz;

    /**
    * 获取类别
    * @param
    * @return
    */
    @ApiOperation("获取类别")
    @PostMapping("v1/intervene/interveneCateg/listInterveneCateg")
    public List<InterveneCategResponse> listInterveneCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return interveneCategBiz.listInterveneCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @ApiOperation("保存类别")
    @PostMapping("v1/intervene/interveneCateg/saveInterveneCateg")
    public Boolean saveInterveneCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) {
        return interveneCategBiz.saveInterveneCateg(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @ApiOperation("删除类别")
    @DeleteMapping("v1/intervene/interveneCateg/delInterveneCateg")
    public Boolean delInterveneCateg(@Validated DelInterveneCategRequest delInterveneCateg ) {
        return interveneCategBiz.delInterveneCateg(delInterveneCateg);
    }


}