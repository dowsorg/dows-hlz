package org.dows.hep.biz.user.experiment;

import org.dows.hep.api.base.indicator.response.ExperimentIndicatorViewBaseInfoRsResponse;
import org.dows.hep.api.user.experiment.response.ExptHealthDocInfoResponse;
import org.springframework.stereotype.Service;

/**
 * 健康档案接口
 * @author : wuzl
 * @date : 2023/9/6 9:38
 */
@Service
public class ExperimentHealthDocBiz {
    /**
     * 获取健康档案左上基本信息
     * @param appId
     * @param experimentPersonId
     * @return
     */
    public ExperimentIndicatorViewBaseInfoRsResponse getBaseInfo(String appId,String experimentInstanceId, String experimentPersonId){

        return null;
    }

    /**
     * 获取健康档案人物指标图
     * @param appId
     * @param experimentPersonId
     * @return
     */
    public ExptHealthDocInfoResponse getIndicatorInfo(String appId,String experimentInstanceId, String experimentPersonId){

        return new ExptHealthDocInfoResponse();
    }
}
