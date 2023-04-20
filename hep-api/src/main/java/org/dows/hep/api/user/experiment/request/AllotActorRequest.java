package org.dows.hep.api.user.experiment.request;

import lombok.Data;
import org.dows.framework.api.uim.AccountInfo;

import java.util.List;
import java.util.Map;

@Data
public class AllotActorRequest {

    private Map<AccountInfo, List<CaseOrgInfo>> actorOrgMap;

    private String experimentInstanceId;

    private String experimentName;
    private String experimentGroupId;

    public static class CaseOrgInfo{


    }
}
