package org.dows.hep.biz.user.experiment;

import org.dows.framework.api.Response;
import org.dows.hep.api.user.experiment.request.SaveOperateInteveneRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:实验:健康干预
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class ExperimentInterveneBiz{
    /**
    * @param
    * @return
    * @说明: 保存干预记录
    * @关联表: OperateIntervene,OperateIndicator
    * @工时: 2H
    * @开发者: wuzl
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean saveOperateIntevene(SaveOperateInteveneRequest saveOperateIntevene ) {
        return Boolean.FALSE;
    }
}