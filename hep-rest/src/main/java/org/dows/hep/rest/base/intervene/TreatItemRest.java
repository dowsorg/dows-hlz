package org.dows.hep.rest.base.intervene;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelTreatItemRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.request.SaveTreatItmeRequest;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.dows.hep.biz.base.intervene.TreatItemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
* @description project descr:干预:治疗项目
*
* @author lait.zhang
* @date 2023年4月23日 上午9:44:34
*/
@RequiredArgsConstructor
@RestController
@Tag(name = "治疗项目", description = "治疗项目")
public class TreatItemRest {
    private final TreatItemBiz treatItemBiz;

    /**
    * 获取治疗项目列表
    * @param
    * @return
    */
    @Operation(summary = "获取治疗项目列表")
    @PostMapping("v1/baseIntervene/treatItem/pageTreatItem")
    public TreatItemResponse pageTreatItem(@RequestBody @Validated FindTreatRequest findTreat ) {
        return treatItemBiz.pageTreatItem(findTreat);
    }

    /**
    * 获取治疗项目信息
    * @param
    * @return
    */
    @Operation(summary = "获取治疗项目信息")
    @PostMapping("v1/baseIntervene/treatItem/infoTreatItem")
    public TreatItemInfoResponse infoTreatItem(@RequestBody @Validated String treatItemId ) {
        return treatItemBiz.infoTreatItem(treatItemId);
    }

    /**
    * 保存治疗项目
    * @param
    * @return
    */
    @Operation(summary = "保存治疗项目")
    @PostMapping("v1/baseIntervene/treatItem/saveTreatItem")
    public Boolean saveTreatItem(@RequestBody @Validated SaveTreatItmeRequest saveTreatItme ) {
        return treatItemBiz.saveTreatItem(saveTreatItme);
    }

    /**
    * 删除治疗项目
    * @param
    * @return
    */
    @Operation(summary = "删除治疗项目")
    @DeleteMapping("v1/baseIntervene/treatItem/delTreatItem")
    public Boolean delTreatItem(@Validated DelTreatItemRequest delTreatItem ) {
        return treatItemBiz.delTreatItem(delTreatItem);
    }


}