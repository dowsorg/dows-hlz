package org.dows.hep.rest.base.intervene;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.FindInterveneCategRequest;
import org.dows.hep.api.base.intervene.request.SaveInterveneCategRequest;
import org.dows.hep.biz.base.intervene.InterveneCategBiz;
import org.dows.hep.biz.vo.CategVO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* @description project descr:干预:干预类别管理
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
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
    public List<CategVO> listInterveneCateg(@RequestBody @Validated FindInterveneCategRequest findInterveneCateg ) {
        return interveneCategBiz.listInterveneCateg(findInterveneCateg);
    }

    /**
    * 保存类别
    * @param
    * @return
    */
    @Operation(summary = "保存类别")
    @PostMapping("v1/baseIntervene/interveneCateg/saveInterveneCateg")
    public Boolean saveInterveneCateg(@RequestBody @Validated SaveInterveneCategRequest saveInterveneCateg ) throws JsonProcessingException {
        return interveneCategBiz.saveInterveneCateg(saveInterveneCateg);
    }

    /**
    * 批量保存类别
    * @param
    * @return
    */
    @Operation(summary = "批量保存类别")
    @PostMapping("v1/baseIntervene/interveneCateg/saveInterveneCategs")
    public Boolean saveInterveneCategs(@RequestBody @Validated List<SaveInterveneCategRequest> saveInterveneCateg ) throws JsonProcessingException {
        return interveneCategBiz.saveInterveneCategs(saveInterveneCateg);
    }

    /**
    * 删除类别
    * @param
    * @return
    */
    @Operation(summary = "删除类别")
    @DeleteMapping("v1/baseIntervene/interveneCateg/delInterveneCateg")
    public Boolean delInterveneCateg(@RequestBody @Validated DelInterveneCategRequest delInterveneCateg ) {
        return interveneCategBiz.delInterveneCateg(delInterveneCateg);
    }


}