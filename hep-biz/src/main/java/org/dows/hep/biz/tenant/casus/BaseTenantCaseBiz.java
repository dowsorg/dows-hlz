package org.dows.hep.biz.tenant.casus;

import lombok.RequiredArgsConstructor;
import org.dows.sequence.api.IdGenerator;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class BaseTenantCaseBiz {
    private static final String LAST_VERSION = "SNAPSHOT";

    private final IdGenerator idGenerator;

    public String getAppId() {
        return "3";
    }

    public String getIdStr() {
        return idGenerator.nextIdStr();
    }

    public String getLastVer() {
        return LAST_VERSION;
    }

    public String getVer(Date date) {
        return String.valueOf((date == null ? new Date() : date).getTime());
    }

}
