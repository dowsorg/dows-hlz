package org.dows.hep.api.tenant.experiment.request;

import lombok.Data;

@Data
public class PageExperimentRequest {

    private Integer pageNo;

    private Integer pageSize;

    private String keyword;

    private String orderBy;
    // 默认降序
    private Boolean desc = true;
}
