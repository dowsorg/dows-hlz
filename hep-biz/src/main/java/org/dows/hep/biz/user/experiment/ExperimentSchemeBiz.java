package org.dows.hep.biz.user.experiment;

import org.dows.framework.api.Response;
import org.dows.hep.api.user.experiment.request.DesignSchemeRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
/**
* @description project descr:实验:实验方案
*
* @author lait.zhang
* @date 2023年4月14日 下午4:47:53
*/
@Service
public class ExperimentSchemeBiz{
    /**
    * @param
    * @return
    * @说明: 设计实验方案
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean designScheme(DesignSchemeRequest designScheme ) {
        return Boolean.FALSE;
    }
    /**
    * @param
    * @return
    * @说明: 提交方案
    * @关联表: 
    * @工时: 2H
    * @开发者: lait
    * @开始时间: 
    * @创建时间: 2023年4月14日 下午4:47:53
    */
    public Boolean submitScheme(String schemeId ) {
        return Boolean.FALSE;
    }
}