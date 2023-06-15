package org.dows.hep.biz.base.risk;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dows.hep.api.base.risk.request.CrowdsInstanceRequest;
import org.dows.hep.entity.CrowdsInstanceEntity;
import org.dows.hep.service.CrowdsInstanceService;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

/**
 * @author jx
 * @date 2023/6/15 14:01
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CrowdsInstanceBiz {
    private final CrowdsInstanceService crowdsInstanceService;

    private final IdGenerator idGenerator;

    /**
     * @param
     * @return
     * @说明: 创建或更新人群类别
     * @关联表:
     * @工时: 4H
     * @开发者: jx
     * @开始时间:
     * @创建时间: 2023年4月23日 上午9:44:34
     */
    @DSTransactional
    public Boolean insertOrUpdateCrows(CrowdsInstanceRequest crowdsInstanceRequest) {
        Boolean flag = false;
        //1、更新
        if (crowdsInstanceRequest.getId() != null) {
            CrowdsInstanceEntity crowdsEntity = CrowdsInstanceEntity
                    .builder()
                    .id(crowdsInstanceRequest.getId())
                    .crowdsId(crowdsInstanceRequest.getCrowdsId())
                    .appId(crowdsInstanceRequest.getAppId())
                    .name(crowdsInstanceRequest.getName())
                    .crowdsFormulaId(crowdsInstanceRequest.getCrowdsFormulaId())
                    .odds(crowdsInstanceRequest.getOdds())
                    .build();
            flag = crowdsInstanceService.updateById(crowdsEntity);
        } else {
            //2、插入
            CrowdsInstanceEntity crowdsEntity = CrowdsInstanceEntity
                    .builder()
                    .crowdsId(idGenerator.nextIdStr())
                    .appId(crowdsInstanceRequest.getAppId())
                    .name(crowdsInstanceRequest.getName())
                    .crowdsFormulaId(crowdsInstanceRequest.getCrowdsFormulaId())
                    .odds(crowdsInstanceRequest.getOdds())
                    .build();
            flag = crowdsInstanceService.save(crowdsEntity);
        }
        return flag;
    }
}
