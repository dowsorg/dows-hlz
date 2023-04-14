package org.dows.hep.rest.intervene;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.dows.framework.api.Response;
import org.dows.hep.api.intervene.request.FindTreatRequest;
import org.dows.hep.api.intervene.response.TreatItemResponse;
import org.dows.hep.api.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.intervene.request.SaveTreatItmeRequest;
import org.dows.hep.api.intervene.request.DelTreatItemRequest;
import org.dows.hep.biz.intervene.TreatItemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* @description project descr:干预:治疗项目
*
* @author lait.zhang
* @date 2023年4月14日 下午3:31:43
*/
@RequiredArgsConstructor
@RestController
@Api(tags = "治疗项目")
public class TreatItemRest {
    private final TreatItemBiz treatItemBiz;

    /**
    * 获取治疗项目列表
    * @param
    * @return
    */
    @ApiOperation("获取治疗项目列表")
    @PostMapping("v1/intervene/treatItem/pageTreatItem")
    public TreatItemResponse pageTreatItem(@RequestBody @Validated FindTreatRequest findTreat ) {
        return treatItemBiz.pageTreatItem(findTreat);
    }

    /**
    * 获取治疗项目信息
    * @param
    * @return
    */
    @ApiOperation("获取治疗项目信息")
    @PostMapping("v1/intervene/treatItem/infoTreatItem")
    public TreatItemInfoResponse infoTreatItem(@RequestBody @Validated String treatItemId ) {
        return treatItemBiz.infoTreatItem(treatItemId);
    }

    /**
    * 保存治疗项目
    * @param
    * @return
    */
    @ApiOperation("保存治疗项目")
    @PostMapping("v1/intervene/treatItem/saveTreatItem")
    public Boolean saveTreatItem(@RequestBody @Validated SaveTreatItmeRequest saveTreatItme ) {
        return treatItemBiz.saveTreatItem(saveTreatItme);
    }

    /**
    * 删除治疗项目
    * @param
    * @return
    */
    @ApiOperation("删除治疗项目")
    @DeleteMapping("v1/intervene/treatItem/delTreatItem")
    public Boolean delTreatItem(@Validated DelTreatItemRequest delTreatItem ) {
        return treatItemBiz.delTreatItem(delTreatItem);
    }


}