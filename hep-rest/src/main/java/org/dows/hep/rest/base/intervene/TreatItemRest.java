package org.dows.hep.rest.base.intervene;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.dows.hep.api.base.intervene.request.DelTreatItemRequest;
import org.dows.hep.api.base.intervene.request.FindTreatRequest;
import org.dows.hep.api.base.intervene.request.SaveTreatItemRequest;
import org.dows.hep.api.base.intervene.request.SetTreatItemStateRequest;
import org.dows.hep.api.base.intervene.response.TreatItemInfoResponse;
import org.dows.hep.api.base.intervene.response.TreatItemResponse;
import org.dows.hep.biz.base.intervene.TreatItemBiz;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public Page<TreatItemResponse> pageTreatItem(@RequestBody @Validated FindTreatRequest findTreat ) {
        return treatItemBiz.pageTreatItem(findTreat);
    }

    /**
    * 获取治疗项目信息
    * @param
    * @return
    */
    @Operation(summary = "获取治疗项目信息")
    @GetMapping("v1/baseIntervene/treatItem/infoTreatItem")
    public TreatItemInfoResponse infoTreatItem( @Validated String treatItemId ) {
        return treatItemBiz.infoTreatItem(treatItemId);
    }

    /**
    * 保存治疗项目
    * @param
    * @return
    */
    @Operation(summary = "保存治疗项目")
    @PostMapping("v1/baseIntervene/treatItem/saveTreatItem")
    public Boolean saveTreatItem(@RequestBody @Validated SaveTreatItemRequest saveTreatItme ) {
        return treatItemBiz.saveTreatItem(saveTreatItme);
    }

    /**
     * 启用禁用治疗项目
     *
     * @param
     * @return
     */
    @Operation(summary = "启用禁用治疗项目")
    @PostMapping("v1/baseIntervene/treatItem/setTreatItemState")
    public Boolean setTreatItemState(@RequestBody @Validated SetTreatItemStateRequest setTreatItemState ) {
        return treatItemBiz.setTreatItemState(setTreatItemState);
    }


    /**
    * 删除治疗项目
    * @param
    * @return
    */
    @Operation(summary = "删除治疗项目")
    @DeleteMapping("v1/baseIntervene/treatItem/delTreatItem")
    public Boolean delTreatItem(@RequestBody @Validated DelTreatItemRequest delTreatItem ) {
        return treatItemBiz.delTreatItem(delTreatItem);
    }


}