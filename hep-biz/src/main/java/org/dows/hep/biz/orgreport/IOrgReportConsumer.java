package org.dows.hep.biz.orgreport;

import org.dows.hep.api.user.experiment.vo.ExptOrgReportNodeDataVO;

/**
 * @author : wuzl
 * @date : 2023/7/18 16:23
 */
public interface IOrgReportConsumer<T> {
    void accept(T report, ExptOrgReportNodeDataVO node);
}
